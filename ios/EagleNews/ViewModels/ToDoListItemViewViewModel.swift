import SwiftUI
internal import Combine
import Foundation
import FirebaseAuth
import FirebaseFirestore
import AudioToolbox

class ToDoListItemViewViewModel: ObservableObject {
    func toggleCheck(item: ToDoListItem) {
        var itemCopy = item
        itemCopy.setDone(!item.isDone)
        
        if !item.isDone {
            itemCopy.pinned = false
        }
        
        guard let uid = Auth.auth().currentUser?.uid else {
            return
        }

        let db = Firestore.firestore()
        let docRef = db.collection("users")
            .document(uid)
            .collection("todos")
            .document(itemCopy.id)
        
        if !item.isDone {
            let itemDate = Date(timeIntervalSince1970: item.dueDate)
            let startOfDay = Calendar.current.startOfDay(for: Date())
            
            if itemDate < startOfDay {
                docRef.delete()
                let generator = UINotificationFeedbackGenerator()
                generator.notificationOccurred(.success)
                AudioServicesPlaySystemSound(1113)
                return
            } else {
                AudioServicesPlaySystemSound(1113)
            }
        }
        
        docRef.setData(item.asDictionary())
        docRef.setData(itemCopy.asDictionary())
    }
}
