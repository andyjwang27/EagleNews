import Foundation

struct ToDoListItem: Codable, Identifiable {
    let id: String
    let title: String
    let dueDate: TimeInterval
    let createdDate: TimeInterval
    let reminderSent: Bool
    var isDone: Bool
    var pinned: Bool
    
    mutating func setDone(_ state: Bool) {
        isDone = state
    }
}
