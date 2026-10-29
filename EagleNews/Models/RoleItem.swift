import Foundation
import FirebaseFirestore

struct RoleItem: Codable, Identifiable, Hashable {
    @DocumentID var id: String?
    var name: String
    var priorityLevel: String
    var type: String
}
