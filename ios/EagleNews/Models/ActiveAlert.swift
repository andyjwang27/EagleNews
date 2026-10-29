import Foundation

enum ActiveAlert: Identifiable {
    case error
    case success
    
    var id: Int {
            switch self {
            case .error: return 0
            case .success: return 1
            }
        }
}
