import SwiftUI
import Foundation
internal import Combine

class SidebarState: ObservableObject {
    @Published var isSideBarOpened = false
}

struct SideBarModel: Identifiable {
    var id = UUID()
    var icon: String
    var title: String
    var tab: TabIcon
    var index: Int
}

enum TabIcon: Hashable {
    case homeView
    case calendarView
    case todoView
    case linksView
    case profileView
    case adminView
    
    var menuIndex: Int {
        switch self {
        case .homeView: return 0
        case .todoView: return 1
        case .calendarView: return 2
        case .linksView: return 3
        case .profileView: return 4
        case .adminView: return 5
        }
    }
}

func sideBarItems(isAdmin: Bool) -> [SideBarModel] {
    var items = [
        SideBarModel(icon: "house", title: "Home", tab: .homeView, index: 0),
        SideBarModel(icon: "list.clipboard", title: "To-Do List", tab: .todoView, index: 1),
        SideBarModel(icon: "calendar", title: "Calendar", tab: .calendarView, index: 2),
        SideBarModel(icon: "link", title: "Links", tab: .linksView, index: 3),
        SideBarModel(icon: "person.circle", title: "Profile", tab: .profileView, index: 4)
    ]
    
    if isAdmin {
        items.append(SideBarModel(
            icon: "person.badge.key.fill",
            title: "Admin",
            tab: .adminView,
            index: 5)
        )
    }
    
    return items
}
