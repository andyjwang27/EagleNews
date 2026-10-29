import Foundation
import FirebaseFirestore

struct EventItem: nonisolated Codable, Identifiable {
    @DocumentID var id: String?
    var title: String
    var startDate: Timestamp
    var endDate: Timestamp
    var pinned: Bool
    var publisher: String
    var publisherId: String
    var tags: [String]
    var isGroup: Bool
    var color: String
    var reminderSent: Bool
    var recipientIds: [String]
    var groupEventId: String?
    var eventMinutesBefore: Int
}
