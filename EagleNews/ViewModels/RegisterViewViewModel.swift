import Foundation
internal import Combine
import FirebaseAuth
import FirebaseFirestore
import FirebaseMessaging
import GoogleSignIn

class RegisterViewViewModel: ObservableObject {
    @Published var name = ""
    @Published var email = ""
    @Published var errorMessage = ""
    @Published var activeRoles: [String] = ["student"]
    @Published var activeAlert: ActiveAlert?
    @Published var eventMinutesBefore = 5
        
    func registerWithGoogle(idToken: String, accessToken: String) {
        guard validate() else {
            activeAlert = .error
            return
        }
        
        let credential = GoogleAuthProvider.credential(
            withIDToken: idToken,
            accessToken: accessToken
        )
        
        Auth.auth().signIn(with: credential) { [weak self] authResult, error in
            DispatchQueue.main.async {
                
                if let error = error as NSError? {
                    
                    // Handles duplicate emails and existing accuonts
                    if let code = AuthErrorCode(rawValue: error.code), code == .emailAlreadyInUse {
                        self?.errorMessage = "An account already exists with this email."
                        self?.activeAlert = .error
                        return
                    }
                    
                    self?.errorMessage = error.localizedDescription
                    self?.activeAlert = .error
                    return
                }
                
                guard let user = authResult?.user else {
                    self?.errorMessage = "Failed to get user."
                    self?.activeAlert = .error
                    return
                }
                
                let userId = user.uid
                
                self?.insertUserRecord(id: userId)
                self?.requestNotificationPermission()
                self?.subscribeToUserRoles(userId: userId)
                
                self?.activeAlert = .success
            }
        }
    }
    
    
    func validate() -> Bool {
        guard !name.trimmingCharacters(in: .whitespaces).isEmpty,
              !email.trimmingCharacters(in: .whitespaces).isEmpty else {
            errorMessage = "Please fill in all fields"
            return false
        }
        
        guard email.contains("@apps.stratford.org") || email.contains("rowdieeagle@gmail.com") else {
            errorMessage = "Please use a Stratford Apps email."
            return false
        }
        return true
    }
    
    private func requestNotificationPermission() {
        UNUserNotificationCenter.current().requestAuthorization(
            options: [.alert, .sound, .badge]
        ) { granted, error in
            print("Notifications granted: \(granted)")
        }
    }
    
    private func subscribeToUserRoles(userId: String) {
        Firestore.firestore()
            .collection("users")
            .document(userId)
            .getDocument { snapshot, error in
                guard let data = snapshot?.data(),
                      let roles = data["activeRoles"] as? [String] else {
                    return
                }
                
                // Subscribing to roles
                for role in roles {
                    let topic = self.topicName(role)
                    print("Subscribing to:", self.topicName(role))
                    Messaging.messaging().subscribe(toTopic: topic) { error in
                        if let error = error {
                            print("Failed to subscribe to \(role): \(error)")
                        } else {
                            print("Subscribed to \(role)")
                        }
                    }
                }
                
                // Subscribing to user's own topic
                Messaging.messaging().subscribe(toTopic: "user_\(userId)") { error in
                    if let error = error {
                        print("Failed to subscribe to personal topic: \(error)")
                    } else {
                        print("Subscribed to topic user_\(userId)")
                    }
                }
                
            }
    }
    
    private func insertUserRecord(id: String) {
        let db = Firestore.firestore()
        
        let newUser = User(
            id: id,
            name: name,
            email: email,
            joined: Date().timeIntervalSince1970,
            activeRoles: activeRoles
        )
        
        db.collection("users")
            .document(id)
            .setData(newUser.asDictionary(), merge: true)
    }
    
    func topicName(_ role: String) -> String {
        role
            .trimmingCharacters(in: .whitespacesAndNewlines)
            .lowercased()
            .replacingOccurrences(
                of: "\\s+",
                with: "_",
                options: .regularExpression
            )
            .replacingOccurrences(
                of: "[^a-z0-9\\-_.~%]",
                with: "",
                options: .regularExpression
            )
    }
}
