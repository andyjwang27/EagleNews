import Foundation
import FirebaseFunctions
internal import Combine
import FirebaseAuth
import FirebaseFirestore
import FirebaseMessaging
import GoogleSignIn
import UIKit

enum DeleteState {
    case idle
    case confirmStart
    case countingDown
    case readyToDelete
}

class ProfileSettingsViewViewModel: ObservableObject {
    @Published var name: String = ""
    @Published var errorMessage: String = ""
    @Published var activeAlert: ActiveAlert?
    @Published var deleteState: DeleteState = .idle
    @Published var countdown: Int = 5
    @Published var user: User? = nil
    
    private var timer: Timer?
    
    func save(completion: @escaping(Bool) -> Void) {
        guard canSave else {
            completion(false)
            return
        }
        
        let db = Firestore.firestore()
        guard let userId = Auth.auth().currentUser?.uid else {
            self.activeAlert = .error
            completion(false)
            return
        }
        
        db.collection("users")
            .document(userId)
            .updateData(["name": name]) { error in
                if let error = error {
                    self.errorMessage = "Error updating name: \(error.localizedDescription)"
                } else {
                    print("Success updating name")
                    completion(true)
                }
            }
        
    }
    
    var canSave: Bool {
        guard !name.trimmingCharacters(in: .whitespaces).isEmpty else {
            errorMessage = "Please fill in all fields."
            return false
        }
        return true
    }
    
    func requestNotificationPermission() {
        UNUserNotificationCenter.current().requestAuthorization(
            options: [.alert, .sound, .badge]
        ) { granted, error in
            print("Notifications granted: \(granted)")
        }
    }
    
    func startDeleteFlow() {
        deleteState = .confirmStart
    }
    
    func beginCountdown() {
        deleteState = .countingDown
        countdown = 5
        
        timer?.invalidate()
        
        timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { [weak self] t in
            guard let self else {
                return
            }
            
            self.countdown -= 1
            
            if self.countdown <= 0 {
                t.invalidate()
                self.timer = nil
                self.deleteState = .readyToDelete
            }
        }
    }
    
    func cancelDelete() {
        timer?.invalidate()
        timer = nil
        deleteState = .idle
        countdown = 5
    }
    
    @MainActor
    func deleteAccount() async {
        guard let user = Auth.auth().currentUser else {
            return }
        
        let userId = user.uid

        do {
            let rootVC = UIApplication.shared.connectedScenes
                .compactMap { $0 as? UIWindowScene }
                .first?
                .windows
                .first?
                .rootViewController

            guard let presentingVC = rootVC else { return }

            let signInResult = try await GIDSignIn.sharedInstance.signIn(
                withPresenting: presentingVC
            )

            guard let idToken = signInResult.user.idToken?.tokenString else { return }

            let credential = GoogleAuthProvider.credential(
                withIDToken: idToken,
                accessToken: signInResult.user.accessToken.tokenString
            )

            try await user.reauthenticate(with: credential)
            
            await unsubscribeFromTopics(userId: userId)

            let functions = Functions.functions()
            _ = try await functions.httpsCallable("deleteUserAccount").call()

            try Auth.auth().signOut()

            print("Account fully deleted")

        } catch {
            print("Delete failed:", error)
        }
    }
    
    private func unsubscribeFromTopics(userId: String) async {
        do {
            let snapshot = try await Firestore.firestore()
                .collection("users")
                .document(userId)
                .getDocument()
            
            let roles = snapshot.data()?["activeRoles"] as? [String] ?? []
            
            for role in roles {
                do {
                    let topic = self.topicName(role)
                    try await Messaging.messaging().unsubscribe(fromTopic: topic)
                    print("Unsubscribed from \(role)")
                } catch {
                    print("Failed to unsubscribe from \(role): \(error)")
                }
            }
            
            do {
                try await Messaging.messaging().unsubscribe(fromTopic: "user_\(userId)")
                print("Unsubscribed from user_\(userId)")
            } catch {
                print("Failed to unsubscribe from user_\(userId): \(error)")
            }
        } catch {
            print("Failed to fetch user roles: \(error)")
        }
    }
    
    func topicName(_ role: String) -> String {
        role
            .trimmingCharacters(in: .whitespacesAndNewlines)
            .lowercased()
            .replacingOccurrences(of: "\\s+", with: "", options: .regularExpression)
            .replacingOccurrences(of: "[^a-z0-9\\-_.~%]", with: "", options: .regularExpression)
    }
    
    func fetchUser() {
        guard let userId = Auth.auth().currentUser?.uid else {
            return
        }
        let db = Firestore.firestore()
        db.collection("users").document(userId).getDocument { [weak self] snapshot, error in
            guard let data = snapshot?.data(), error == nil else {
                return
            }
            
            DispatchQueue.main.async {
                self?.user = User(
                    id: data["id"] as? String ?? "",
                    name: data["name"] as? String ?? "",
                    email: data["email"] as? String ?? "",
                    joined: data["joined"] as? TimeInterval ?? 0,
                    activeRoles: data["activeRoles"] as? [String] ?? []
                )
            }
        }
    }
    
}
