import SwiftUI

struct AdminView: View {
    @State var selectedTab = 0
    @Binding var showMenu: Bool
    
    var body: some View {
        TabView(selection: $selectedTab) {
            CreateView(showMenu: $showMenu)
                .tabItem {
                    Image(systemName: "pencil")
                    Text("Article")
                }
                .tag(0)
            AdminInfoView(showMenu: $showMenu, isSheet: false)
                .tabItem {
                    Image(systemName: "i.circle")
                    Text("Roles")
                }
                .tag(1)
            RoleChangeView(showMenu: $showMenu)
                .tabItem {
                    Image(systemName: "hand.tap")
                    Text("Assign")
                }
                .tag(2)
        }
    }
}
