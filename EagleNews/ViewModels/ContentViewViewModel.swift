import Foundation
import FirebaseAuth
import FirebaseFirestore
internal import Combine
import FirebaseMessaging

class ContentViewViewModel: ObservableObject {
    @Published var currentUserId: String = ""
    @Published var user: User? = nil
    @Published var isAdmin: Bool = false
    @Published var isClubLeader: Bool = false
    private var handler: AuthStateDidChangeListenerHandle?

    init() {
        self.handler = Auth.auth().addStateDidChangeListener { [weak self] _, user in
            DispatchQueue.main.async {
                self?.currentUserId = user?.uid ?? ""
                if user != nil {
                    self?.testAdmin()
                    self?.testClubLeader()
                } else {
                    self?.isAdmin = false
                    self?.isClubLeader = false
                }
            }
        }
    }
    
    func logout() {
        guard let userId = Auth.auth().currentUser?.uid else {
            return
        }
        
        Task {
            await unsubscribeFromTopics(userId: userId)
            
            do {
                try Auth.auth().signOut()
                await MainActor.run {
                    self.currentUserId = ""
                }
            } catch {
                print("Logout error:", error)
            }
        }
    }

    var isSignedIn: Bool {
        !currentUserId.isEmpty
    }
        
    // Used for Admin Permissions
    func testAdmin(){
        guard let userId = Auth.auth().currentUser?.uid else {
            return
        }
        let db = Firestore.firestore()
        db.collection("users").document(userId).getDocument { [weak self] snapshot, error in
            guard let data = snapshot?.data(),
                  error == nil,
                  let roles = data["activeRoles"] as? [String]
            else {
                DispatchQueue.main.async {
                    self?.isAdmin = false
                }
                return
            }
            
            DispatchQueue.main.async {
                self?.isAdmin = roles.contains("admin")
            }
        }
    }
    
    func testClubLeader(){
        guard let userId = Auth.auth().currentUser?.uid else {
            return
        }
        let db = Firestore.firestore()
        db.collection("users").document(userId).getDocument { [weak self] snapshot, error in
            guard let data = snapshot?.data(),
                  error == nil,
                  let roles = data["activeRoles"] as? [String]
            else {
                DispatchQueue.main.async {
                    self?.isClubLeader = false
                }
                return
            }
            
            DispatchQueue.main.async {
                self?.isClubLeader = roles.contains("clubLeader")
            }
        }
    }
    

    deinit {
        if let handler = handler {
            Auth.auth().removeStateDidChangeListener(handler)
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
                    try await Messaging.messaging().unsubscribe(fromTopic: role)
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
}
