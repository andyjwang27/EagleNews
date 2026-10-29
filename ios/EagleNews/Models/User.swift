import Foundation

struct User: Identifiable, Codable, Hashable {
    let id: String
    let name: String
    let email: String
    let joined: TimeInterval
    let activeRoles: [String]
}
