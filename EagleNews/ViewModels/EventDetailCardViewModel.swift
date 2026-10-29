import Foundation
internal import Combine
import FirebaseFirestore

class EventDetailCardViewModel: ObservableObject {
    private let userId: String
    
    init(userId: String) {
        self.userId = userId
    }
    
    // Delete To-Do List Item
    func delete(id: String) {
        let db = Firestore.firestore()
        
        db.collection("users")
            .document(userId)
            .collection("events")
            .document(id)
            .delete()
    }
    
    func togglePin(event: EventItem) {
        guard let id = event.id else {
            return
        }
        let db = Firestore.firestore()
        
        db.collection("users")
            .document(userId)
            .collection("events")
            .document(id)
            .updateData([
                "pinned": !event.pinned
            ]) { error in
                if let error = error {
                    print("Error updating pinned \(error)")
                }
            }
    }
}
