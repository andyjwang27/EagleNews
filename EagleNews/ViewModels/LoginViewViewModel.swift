import FirebaseAuth
import FirebaseCore
import FirebaseFirestore
import FirebaseMessaging
import UserNotifications
import SwiftUI
import Foundation
import GoogleSignInSwift
import GoogleSignIn
internal import Combine

class LoginViewViewModel: ObservableObject {
    @Published var errorMessage = ""
    @Published var showAlert = false
    @Published var isLoading = false
    @Published var showRegister = false
    
    func signInWithGoogle() {
        guard let clientID = FirebaseApp.app()?.options.clientID else {
            self.showError("Missing Firebase Client ID")
            return
        }
        
        let config = GIDConfiguration(clientID: clientID)
        GIDSignIn.sharedInstance.configuration = config
        
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootViewController = windowScene.windows.first?.rootViewController else {
            self.showError("Unable to access root view controller")
            return
        }
        
        isLoading = true
        
        GIDSignIn.sharedInstance.signIn(withPresenting: rootViewController) { [weak self] result, error in
            guard let self = self else {
                return
            }
            
            if let error = error {
                self.showError(error.localizedDescription)
                return
            }
            
            guard let user = result?.user,
                  let idToken = user.idToken?.tokenString,
                  let email = user.profile?.email else {
                self.showError("Failed to retrieve Google credentials.")
                return
            }
            
            guard email.hasSuffix("@apps.stratford.org") || email.lowercased() == "rowdieeagle@gmail.com" else {
                self.showError("Please use your Stratford account")
                GIDSignIn.sharedInstance.signOut()
                self.isLoading = false
                return
            }
            
            let credential = GoogleAuthProvider.credential(
                withIDToken: idToken,
                accessToken: user.accessToken.tokenString
            )
            
            Auth.auth().signIn(with: credential) { authResult, error in
                
                if let error = error {
                    self.showError(error.localizedDescription)
                    return
                }
                
                guard let firebaseUser = authResult?.user else {
                    self.showError("Failed to retrieve Firebase user.")
                    return
                }
                
                let uid = firebaseUser.uid
                
                Firestore.firestore()
                    .collection("users")
                    .document(uid)
                    .getDocument { snapshot, error in

                        if let error = error {
                            self.showError(error.localizedDescription)
                            return
                        }
                        
                        guard snapshot?.data() != nil else {
                            self.showError("No account exists for this user.")

                            try? Auth.auth().signOut()
                            GIDSignIn.sharedInstance.signOut()

                            self.isLoading = false
                            return
                        }

                        Messaging.messaging().token { token, error in
                            if let error = error {
                                self.showError("Failed to fetch FCM token")
                                print("❌ Failed to fetch FCM token:", error)
                            }

                            if let token = token {
                                print("🔥 FORCED FCM TOKEN:", token)

                                Firestore.firestore()
                                    .collection("users")
                                    .document(uid)
                                    .setData(["fcmToken": token], merge: true)
                            }

                            self.subscribeToUserRoles(userId: uid)

                            DispatchQueue.main.async {
                                self.isLoading = false
                                self.errorMessage = ""
                                self.showAlert = false
                            }
                            print("User signed in successfully")
                        }
                    }
            }
        }
    }
    
    private func subscribeToUserRoles(userId: String) {
        Firestore.firestore()
            .collection("users")
            .document(userId)
            .getDocument { snapshot, error in

                guard let data = snapshot?.data(),
                      let roles = data["activeRoles"] as? [String] else {
                    print("No roles found for user")
                    return
                }

                for role in roles {
                    let topic = self.topicName(role)

                    Messaging.messaging().subscribe(toTopic: topic) { error in
                        if let error = error {
                            print("Failed to subscribe to \(topic): \(error)")
                        } else {
                            print("Subscribed to \(topic)")

                            var topics = UserDefaults.standard.stringArray(
                                forKey: "SubscribedTopics"
                            ) ?? []

                            if !topics.contains(topic) {
                                topics.append(topic)
                                UserDefaults.standard.set(
                                    topics,
                                    forKey: "SubscribedTopics"
                                )
                            }
                        }
                    }
                }

                let userTopic = "user_\(userId)"

                Messaging.messaging().subscribe(toTopic: userTopic) { error in
                    if let error = error {
                        print("❌ Failed to subscribe to \(userTopic): \(error)")
                    } else {
                        print("✅ Subscribed to:", userTopic)

                        var topics = UserDefaults.standard.stringArray(
                            forKey: "SubscribedTopics"
                        ) ?? []

                        if !topics.contains(userTopic) {
                            topics.append(userTopic)
                            UserDefaults.standard.set(topics,
                                                      forKey: "SubscribedTopics")
                        }

                        print(
                            "Saved topics:",
                            UserDefaults.standard.stringArray(
                                forKey: "SubscribedTopics"
                            ) ?? []
                        )
                    }
                }
            }
    }
    
    private func requestNotificationPermission() {
        UNUserNotificationCenter.current().requestAuthorization(
            options: [.alert, .sound, .badge]
        ) { granted, error in
            print("Notifications granted: \(granted)")
        }
    }
    
    private func showError(_ message: String) {
        DispatchQueue.main.async {
            self.isLoading = false
            self.errorMessage = message
            self.showAlert = true
        }
    }
    
    func topicName(_ role: String) -> String {
        role
            .trimmingCharacters(in: .whitespacesAndNewlines)
            .lowercased()
            .replacingOccurrences(of: "\\s+", with: "", options: .regularExpression)
            .replacingOccurrences(of: "[^a-z0-9\\-_.~%]", with: "", options: .regularExpression)
    }
}
