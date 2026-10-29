import Foundation
import FirebaseFirestore
internal import Combine

enum ToDoSheet: Identifiable {
    case newItem
    case editToDo

    var id: String {
        switch self {
        case .newItem: return "newItem"
        case .editToDo: return "editToDo"
        }
    }
}

class ToDoListViewViewModel: ObservableObject {
    @Published var todoSheet: ToDoSheet?
    private let userId: String
    
    init(userId: String) {
        self.userId = userId
    }
    
    func delete(id: String) {
        let db = Firestore.firestore()
        
        db.collection("users")
            .document(userId)
            .collection("todos")
            .document(id)
            .delete()
    }
    
    func togglePin(item: ToDoListItem) {
        let id = item.id
        let newValue = !item.pinned
        let db = Firestore.firestore()
        
        db.collection("users")
            .document(userId)
            .collection("todos")
            .document(id)
            .updateData([
                "pinned": newValue
            ]) { error in
                if let error = error {
                    print("Error updating pinned: \(error)")
                }
            }
    }
    
}
