import FirebaseFirestore

struct Link: Identifiable, Codable, Sendable {
    @DocumentID var id: String?
    let name: String
    let link: String
    let tags: [String]
}
