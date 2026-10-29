import Foundation
internal import Combine
import FirebaseMessaging
import FirebaseAuth
import FirebaseFirestore

class AssignRolesViewViewModel: ObservableObject {
    @Published var selectedUsers: Set<User> = []
    @Published var selectedRoles: [String] = []
    @Published var activeAlert: ActiveAlert?
    @Published var errorMessage: String = ""
    @Published var didSucceed: Bool = false
    
    func assign() {
        let db = Firestore.firestore()
        
        guard validate() else {
            self.activeAlert = .error
            return
        }
        
        for user in selectedUsers {
            let userRef = db.collection("users").document(user.id)
            
            userRef.updateData([
                "activeRoles": FieldValue.arrayUnion(selectedRoles)
            ]) { error in
                if let error = error {
                    print("Error assigning roles to user \(user.id): \(error)")
                    self.errorMessage = error.localizedDescription
                    self.activeAlert = .error
                } else {
                    print("Successfully assigned roles for user \(user.id)")
                    self.errorMessage = ""
                    self.didSucceed = true
                    self.activeAlert = .success
                }
            }
        }
        
        errorMessage = ""
    }
    
    func validate() -> Bool{
        guard !selectedUsers.isEmpty && !selectedRoles.isEmpty else {
            errorMessage = "Please select at least one user and one role."
            return false
        }
        
        return true
    }
    
    
}
