import SwiftUI
internal import Combine
import Firebase
import FirebaseAuth
import FirebaseFirestore
import FirebaseStorage

class AddArticleViewViewModel: ObservableObject {
    @Published var title: String = ""
    @Published var summary: String = ""
    @Published var publishedAt: Date = Date()
    @Published var tags: [String] = []
    @Published var publisher: String = ""
    @Published var publisherId: String = ""
    @Published var errorMessage: String = ""
    @Published var pinned: Bool = false
    @Published var user: User? = nil
    @Published var selectRolesView: Bool = false
    @Published var activeAlert: ActiveAlert?
        
    let db = Firestore.firestore()
        
    func fetchUser() {
        guard let userId = Auth.auth().currentUser?.uid else {
            return
        }
        let db = Firestore.firestore()
        db.collection("users").document(userId).getDocument { [weak self] snapshot, error in
            guard let data = snapshot?.data(), error == nil else {
                return
            }
            
            DispatchQueue.main.async {
                self?.user = User(
                    id: data["id"] as? String ?? "",
                    name: data["name"] as? String ?? "",
                    email: data["email"] as? String ?? "",
                    joined: data["joined"] as? TimeInterval ?? 0,
                    activeRoles: data["activeRoles"] as? [String] ?? []
                )
            }
        }
    }
    
    func addArticle(imageURLs: [String], completion: @escaping (Bool) -> Void) {
        
        guard validate() else {
            completion(false)
            return
        }
        
        let articleData: [String: Any] = [
            "title": title.trimmingCharacters(in: .whitespacesAndNewlines),
            "summary": summary.trimmingCharacters(in: .whitespacesAndNewlines),
            "publishedAt": Timestamp(date: publishedAt),
            "tags": tags,
            "imageURLs": imageURLs,
            "publisher": user?.name ?? "",
            "publisherId": Auth.auth().currentUser?.uid ?? "",
            "pinned": pinned
        ]
        
        errorMessage = ""
        
        db.collection("articles").addDocument(data: articleData) { [self] error in
            if let error = error {
                errorMessage = error.localizedDescription
            } else {
                completion(true)
                errorMessage = ""
                title = ""
                summary = ""
                tags = []
                publishedAt = Date()
                publisher = ""
                publisherId = ""
                pinned = false
            }
        }
    }
    
    private func validate() -> Bool {
        errorMessage = ""
        
        guard !title.trimmingCharacters(in: .whitespaces).isEmpty,
                !summary.trimmingCharacters(in: .whitespaces).isEmpty,
              !tags.isEmpty else {
            
            errorMessage = "Please fill in all fields"
            return false
        }
        
        return true
    }
    
    func uploadImage(image: UIImage, completion: @escaping (String?) -> Void) {
        guard let imageData = image.jpegData(compressionQuality: 0.7) else {
            completion(nil)
            return
        }
        
        let fileName = UUID().uuidString
        let ref = Storage.storage()
            .reference()
            .child("articleImages/\(fileName).jpg")
        
        ref.putData(imageData) { _, error in
            if error != nil {
                completion(nil)
                return
            }
            
            ref.downloadURL { url, error in
                completion(url?.absoluteString)
            }
        }
    }
    
    func uploadImages(images: [UIImage], completion: @escaping ([String]) -> Void) {
        var urls: [String] = []
        let group = DispatchGroup()
        
        for image in images {
            group.enter()
            
            uploadImage(image: image) { url in
                
                if let url {
                    urls.append(url)
                }
                
                group.leave()
            }
            
        }
        
        group.notify(queue: .main) {
            completion(urls)
        }
    }
}
