import Foundation
internal import Combine
import FirebaseFirestore
import FirebaseAuth

class EditToDoViewViewModel: ObservableObject {
    @Published var title: String = ""
    @Published var dueDate = Date()
    @Published var activeAlert: ActiveAlert?
    @Published var errorMessage: String = ""
    private let todoId: String
    
    init(selectedToDo: ToDoListItem) {
        self.todoId = selectedToDo.id
        self.title = selectedToDo.title
        self.dueDate = Date(timeIntervalSince1970: selectedToDo.dueDate)
    }
    
    func update(completion: @escaping (Bool) -> Void) {
        guard canSave else {
            completion(false)
            return
        }
        
        // Get currentUserID
        guard let uId = Auth.auth().currentUser?.uid else {
            completion(false)
            return
        }
        
        
        // Save model to database
        let db = Firestore.firestore()
        db.collection("users")
            .document(uId)
            .collection("todos")
            .document(todoId)
            .updateData([
                "title": title,
                "dueDate": dueDate.timeIntervalSince1970
            ]) { error in
                if let error = error {
                    self.errorMessage = error.localizedDescription
                    print("Error updating todo: \(error.localizedDescription)")
                    completion(false)
                } else {
                    print("Successfully updated todo")
                    completion(true)
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
