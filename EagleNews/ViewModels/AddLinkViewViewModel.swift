import SwiftUI
internal import Combine
import Firebase
import FirebaseAuth
import FirebaseFirestore
import FirebaseStorage

class AddLinkViewViewModel: ObservableObject {
    @Published var name: String = ""
    @Published var link: String = ""
    @Published var tags: [String] = []
    @Published var errorMessage: String = ""
    @Published var showingInfoView = false
    @Published var selectRolesView: Bool = false
    @Published var activeAlert: ActiveAlert?
        
    let db = Firestore.firestore()
        
    func addLink(completion: @escaping (Bool) -> Void) {
        
        guard validate() else {
            completion(false)
            return
        }
        
        var formattedLink = link.trimmingCharacters(in: .whitespacesAndNewlines)

        if !formattedLink.lowercased().hasPrefix("http://") &&
            !formattedLink.lowercased().hasPrefix("https://") {
            formattedLink = "https://" + formattedLink
        }
        
        let linkData: [String: Any] = [
            "name": name.trimmingCharacters(in: .whitespacesAndNewlines),
            "link": formattedLink.trimmingCharacters(in: .whitespacesAndNewlines),
            "tags": tags
        ]
        
        errorMessage = ""
        
        db.collection("links").addDocument(data: linkData) { [self] error in
            if let error = error {
                errorMessage = error.localizedDescription
                completion(false)
            } else {
                completion(true)
                errorMessage = ""
                name = ""
                link = ""
                tags = []
            }
        }
    }
    
    private func validate() -> Bool {
        
        errorMessage = ""
        
        guard !name.trimmingCharacters(in: .whitespaces).isEmpty,
                !link.trimmingCharacters(in: .whitespaces).isEmpty,
              !tags.isEmpty else {
            
            errorMessage = "Please fill in all fields"
            return false
        }
        
        // Check if the link works
        var formattedLink = link.trimmingCharacters(in: .whitespacesAndNewlines)
        
        if !formattedLink.lowercased().hasPrefix("http://") && !formattedLink.lowercased().hasPrefix("https://") {
            formattedLink = "https://" + formattedLink
        }
        
        guard let url = URL(string: formattedLink),
            url.scheme != nil,
            url.host != nil else {
            errorMessage = "Invalid URL"
            return false
        }
        
        return true
    }
}
