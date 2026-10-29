import Foundation
import FirebaseAuth
import FirebaseFirestore
internal import Combine

class NewItemViewViewModel: ObservableObject {
    @Published var title = ""
    @Published var dueDate = Date()
    @Published var activeAlert: ActiveAlert?
    @Published var errorMessage = ""
    
    // Save our item
    func save(completion: @escaping (Bool) -> Void) {
        guard canSave else {
            completion(false)
            return
        }
        
        // Get currentUserID
        guard let uId = Auth.auth().currentUser?.uid else {
            completion(false)
            return
        }
        
        // Create model
        let newId = UUID().uuidString
        let newItem = ToDoListItem(
            id: newId,
            title: title,
            dueDate: dueDate.timeIntervalSince1970,
            createdDate: Date().timeIntervalSince1970,
            reminderSent: false,
            isDone: false,
            pinned: false
        )
        
        // Save model to database
        let db = Firestore.firestore()
        db.collection("users")
            .document(uId)
            .collection("todos")
            .document(newId)
            .setData(newItem.asDictionary()) { error in
                DispatchQueue.main.async {
                    completion(error == nil)
                }
            }
        
        errorMessage = ""
        
    }
    
    // Ensures all fields are filled
    var canSave: Bool {
        guard !title.trimmingCharacters(in: .whitespaces).isEmpty else {
            errorMessage = "Please fill in all fields."
            return false
        }
        // Deals with time zones; -86400 is a day's worth of seconds
        guard dueDate >= Date().addingTimeInterval(-86400) else {
            errorMessage = "Please enter a valid date."
            return false
        }
        return true
    }
}
