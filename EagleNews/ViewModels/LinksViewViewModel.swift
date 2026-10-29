import SwiftUI
internal import Combine
import FirebaseFirestore

@MainActor
class LinksViewViewModel: ObservableObject {
    
    @Published var links: [Link] = []
    private var listener: ListenerRegistration?
    private let db = Firestore.firestore()
    
    func startListeningForLinks(userId: String) {
        db.collection("users").document(userId).getDocument { [weak self] snapshot,_ in
            guard let roles = snapshot?.data()?["activeRoles"] as? [String] else {
                return
            }
            
            Task { @MainActor [weak self] in
                self?.listenForLinks(activeRoles: roles)
            }
        }
    }
    
    func listenForLinks(activeRoles: [String]) {
        listener?.remove()
        
        listener = db.collection("links")
            .addSnapshotListener { [weak self] snapshot, error in
                guard let self, let documents = snapshot?.documents else {
                    return
                }
                
                self.links = documents.compactMap { document in
                    guard let link = try? document.data(as: Link.self) else {
                        return nil
                    }
                    
                    return !Set(link.tags).isDisjoint(with: activeRoles) ? link : nil
                }
            }
    }
    
    
    /*func fetchLinks() {
        db.collection("links").getDocuments { snapshot, error in
                
            if let error = error {
                print("Error fetching links: \(error)")
                return
            }
                
            guard let documents = snapshot?.documents else {
                return
            }
                
            Task { @MainActor in
                self.links = documents.compactMap { document in
                    try? document.data(as: Link.self)
                }
            }
        }
    }*/
    
    func deleteLink(_ link: Link) {
        guard let id = link.id else {
            return
        }
        
        db.collection("links").document(id).delete { error in
            if let error = error {
                print("Error deleting link: \(error)")
            } else {
                Task { @MainActor in
                    self.links.removeAll { $0.id == id }
                }
            }
        }
    }
}
