import FirebaseFirestore

struct Article: Identifiable, Codable {
    @DocumentID var id: String?
    var title: String
    var summary: String
    var publishedAt: TimeInterval
    var tags: [String]
    var imageURLs: [String]
    var publisher: String
    var publisherId: String
    var pinned: Bool
}

extension Article {
    init?(from document: QueryDocumentSnapshot) {
        let data = document.data()
        
        guard
            let title = data["title"] as? String,
            let summary = data["summary"] as? String,
            let publisher = data["publisher"] as? String,
            let publisherId = data["publisherId"] as? String,
            let timestamp = data["publishedAt"] as? Timestamp,
            let pinned = data["pinned"] as? Bool
        else {
            return nil
        }
        
        self.id = document.documentID
        self.title = title
        self.summary = summary
        self.publishedAt = timestamp.dateValue().timeIntervalSince1970
        self.tags = data["tags"] as? [String] ?? []
        self.imageURLs = data["imageURLs"] as? [String] ?? []
        self.publisher = publisher
        self.publisherId = publisherId
        self.pinned = pinned
    }
}
