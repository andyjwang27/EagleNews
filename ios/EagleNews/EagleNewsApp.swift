import SwiftUI
import FirebaseAuth
import FirebaseCore
import FirebaseFirestore
import AudioToolbox

@main
struct EagleNewsApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @StateObject var router = AppRouter()
    
    init() {
        FirebaseApp.configure()
        
        preloadAppScenes()
        
        let generator = UIImpactFeedbackGenerator()
        generator.prepare()
        
    }
    
    @StateObject private var authViewModel = ContentViewViewModel()
    
    var body: some Scene {
        WindowGroup {
            ContentView(selectedItem: .homeView)
                .environmentObject(authViewModel)
                .environmentObject(router)
                .preferredColorScheme(.light)
        }
    }
    
    func preloadAppScenes() {
        let db = Firestore.firestore()
        _ = Auth.auth().currentUser
        
        db.collection("users")
            .limit(to: 1)
            .getDocuments { _, _ in }
        
        db.settings = db.settings
        
    }
}
