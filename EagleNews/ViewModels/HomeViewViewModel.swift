import FirebaseFirestore
internal import Combine
import Foundation

class HomeViewViewModel: ObservableObject {
    @Published var articles: [Article] = []
    
    private var db = Firestore.firestore()
    private var listener: ListenerRegistration?
    
    private var hasStartedListening = false

    func startListeningForUser(userId: String) {
        guard !hasStartedListening else { return }
        hasStartedListening = true

        db.collection("users").document(userId).getDocument { [weak self] snapshot, _ in
            guard
                let roles = snapshot?.data()?["activeRoles"] as? [String]
            else { return }
            self?.listenForArticles(activeRoles: roles)
        }
    }
    
    func listenForArticles(activeRoles: [String]) {
        listener?.remove()
        
        // Real-Time Updates
        listener = db.collection("articles")
            .order(by: "pinned", descending: true)
            .order(by: "publishedAt", descending: true)
            .addSnapshotListener { [weak self] snapshot, _ in
                guard let self, let docs = snapshot?.documents else {
                    return
                }
                
                self.articles = docs.compactMap { doc in
                    guard let article = Article(from: doc) else {
                        return nil
                    }
                    
                    return !Set(article.tags).isDisjoint(with: activeRoles) ? article: nil
                
                }
            }
    }
    
    func deleteArticle(articleId: String, completion: ((Error?) -> Void)? = nil) {
        db.collection("articles").document(articleId).delete { error in
            DispatchQueue.main.async {
                completion?(error)
            }
        }
    }
    
    deinit {
        listener?.remove()
    }
    
    func togglePin(article: Article) {
        guard let articleId = article.id else {
            return
        }
        
        db.collection("articles")
            .document(articleId)
            .updateData([
                "pinned": !article.pinned
            ]) { error in
                if let error = error {
                    print("Error updating pin status \(error)")
                }
            }
    }
}
