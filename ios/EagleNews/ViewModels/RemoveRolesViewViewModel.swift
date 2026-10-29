import Foundation
internal import Combine
import FirebaseFirestore
import FirebaseMessaging

class RemoveRolesViewViewModel: ObservableObject {
    @Published var errorMessage: String = ""
    @Published var successMessage: String = ""
    @Published var activeAlert: ActiveAlert?
    @Published var didSucceed: Bool = false
    
    func removeRoles(from user: User, rolesToDelete: [String]) {
        let db = Firestore.firestore()
        let userRef = db.collection("users").document(user.id)
        
        var missingRoles: [String] = []
        var remainingRoles = user.activeRoles
            
        for role in rolesToDelete {
            if let index = remainingRoles.firstIndex(of: role) {
                remainingRoles.remove(at: index)
            } else {
                missingRoles.append(role)
            }
        }
            
        userRef.updateData([
            "activeRoles": remainingRoles
        ]) { error in
                
            if let error = error {
                self.errorMessage = error.localizedDescription
                self.successMessage = ""
                self.activeAlert = .error
                return
            }
            
            Task {
                await self.unsubscribeFromTopics(userId: user.id, roles: rolesToDelete)
            }
                
            self.successMessage = "Roles successfully removed."
                
            if !missingRoles.isEmpty {
                self.successMessage += "\nWarning: User did not have: \(missingRoles.joined(separator: ", "))"
            }
                
            self.errorMessage = ""
            self.didSucceed = true
            self.activeAlert = .success
        }
        
    }
    
    private func unsubscribeFromTopics(userId: String, roles: [String]) async {
        for role in roles {
            do {
                try await Messaging.messaging().unsubscribe(fromTopic: role)
                print("Unsubscribed from \(role)")
            } catch {
                print("Failed to unsubscribe from \(role): \(error)")
            }
        }
    }
}
