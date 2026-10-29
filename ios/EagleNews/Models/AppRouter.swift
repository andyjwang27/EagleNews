import SwiftUI
internal import Combine
import Foundation

class AppRouter: ObservableObject {
    @Published var selectedTab: Int = 0
}
