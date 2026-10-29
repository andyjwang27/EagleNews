import SwiftUI
internal import Combine
import FirebaseFirestore
import FirebaseAuth

class EventsSettingsViewViewModel: ObservableObject {
    @Published var chosenTime = Date()
    @Published var errorMessage = ""
    @Published var activeAlert: ActiveAlert?
    @Published var user: User? = nil
    
    init() {
        let calendar = Calendar.current
        var components = calendar.dateComponents([.year, .month, .day], from: Date())
        
        components.hour = 0
        components.minute = 5
        
        let defaultTime = calendar.date(from: components) ?? Date()
        
        _chosenTime = Published(initialValue: defaultTime)
    }
    
    
    private var eventMinutesBefore: Int {
        let components = Calendar.current.dateComponents([.hour, .minute], from: chosenTime)
        let hours = components.hour ?? 0
        let minutes = components.minute ?? 0
        return (hours * 60) + minutes
    }
        
    func save(completion: @escaping (Bool) -> Void) {
        guard canSave else {
            completion(false)
            return
        }
        
        let db = Firestore.firestore()
        
        guard let userId = Auth.auth().currentUser?.uid else {
            completion(false)
            return
        }
        
        db.collection("users").document(userId).updateData(["eventMinutesBefore": eventMinutesBefore]) { error in
            if let error = error {
                print("Error updating user: \(error.localizedDescription)")
            } else {
                print("Successful update")
                completion(true)
            }
        }
        
        fetchUser()
        errorMessage = ""
    }
    
    var canSave: Bool {
        if eventMinutesBefore < 5 {
            errorMessage = "Please enter a time of at least 5 minutes."
            return false
        }
            
        return true
    }
    
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
}

