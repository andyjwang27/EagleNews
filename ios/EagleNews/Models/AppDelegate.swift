import FirebaseMessaging
import FirebaseAuth
import FirebaseCore
import FirebaseFirestore
import UserNotifications
import UIKit
import GoogleSignIn


class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self

        application.registerForRemoteNotifications()
        
        GIDSignIn.sharedInstance.configuration = GIDConfiguration(
            clientID: FirebaseApp.app()?.options.clientID ?? ""
        )
        
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            print("Push permission:", granted)
        }

        return true
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        print("APNs TOKEN RECEIVED")
        Messaging.messaging().apnsToken = deviceToken
        UserDefaults.standard.set(true, forKey: "HasAPNSToken")
        print("APNs token set in Firebase Messaging")
    }

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let token = fcmToken else {
            return
        }
        
        print("FCM Token: \(token)")
        saveFCMToken(token)
    }
    
    func saveFCMToken(_ token: String) {
        guard let uid = Auth.auth().currentUser?.uid else {
            return
        }
        
        Firestore.firestore()
            .collection("users")
            .document(uid)
            .setData(["fcmToken": token], merge: true)
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification) async -> UNNotificationPresentationOptions {
        return [.banner, .sound, .badge]
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHnadler: @escaping() -> Void) {
        let userInfo = response.notification.request.content.userInfo
        
        print("Notification tapped:", userInfo)
        
        NotificationCenter.default.post(
            name: .notificationTapped,
            object: nil,
            userInfo: userInfo
        )
    }
}

extension Notification.Name {
    static let notificationTapped = Notification.Name("notificationTapped")
}
