import SwiftUI
internal import Combine
import FirebaseAuth
import FirebaseFirestore
import Foundation

class ProfileViewViewModel: ObservableObject {
    @Published var user: User? = nil
    @Published var showingSettingsView = false
    
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
    
    func topicName(_ role: String) -> String {
        role
            .trimmingCharacters(in: .whitespacesAndNewlines)
            .lowercased()
            .replacingOccurrences(
                of: "\\s+",
                with: "",
                options: .regularExpression
            )
            .replacingOccurrences(
                of: "[^a-z0-9\\-_.~%]",
                with: "",
                options: .regularExpression
            )
    }
}
