import SwiftUI
import FirebaseFirestore

struct ToDoListView: View {
    @Binding var showMenu: Bool
    @State var selectedTab = 0
    
    private let userId: String
    
    init (userId: String, showMenu: Binding<Bool>) {
        self.userId = userId
        self._showMenu = showMenu
    }
    
    public var body: some View {
        TabView(selection: $selectedTab) {
            ToDoTodayView(userId: userId, showMenu: $showMenu)
                .tabItem {
                    Image(systemName: "calendar.badge")
                    Text("Due Today")
                }
                .tag(0)
            ToDoWeekView(userId: userId, showMenu: $showMenu)
                .tabItem {
                    Image(systemName: "calendar.circle")
                    Text("Due This Week")
                }
                .tag(1)
            ToDoAllView(userId: userId, showMenu: $showMenu)
                .tabItem {
                    Image(systemName: "calendar")
                    Text("All Due")
                }
                .tag(2)
        }
    }
}
