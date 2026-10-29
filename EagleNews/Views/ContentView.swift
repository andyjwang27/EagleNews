import SwiftUI
import Firebase

// Instance variables
let version = "1.00"

public struct ContentView: View {
    @EnvironmentObject private var viewModel: ContentViewViewModel
    @State var selectedItem: TabIcon = .homeView
    @State var showMenu = false
    @State private var preloadText = ""
    @FocusState private var focused: Bool
    
    init(selectedItem: TabIcon) {
        self.selectedItem = selectedItem
    }
    
    public var body: some View {
        ZStack {
            KeyboardWarmupView()
                .frame(width: 0, height: 0)
            Group {
                if viewModel.isSignedIn, !viewModel.currentUserId.isEmpty {
                    accountView
                } else {
                    LoginView()
                }
            }
            .overlay {
                TextField("", text: $preloadText)
                    .focused($focused)
                    .opacity(0)
                    .frame(width: 1, height: 1)
            }
            .onAppear {
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                    focused = true
                    
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                        focused = false
                    }
                    
                    
                }
            }
        }
    }

    @ViewBuilder
    var accountView: some View {
        ZStack {
            Group {
                switch selectedItem {
                case .homeView:
                    HomeView(userId: viewModel.currentUserId, showMenu: $showMenu)
                case .todoView:
                    ToDoListView(userId: viewModel.currentUserId, showMenu: $showMenu)
                case .calendarView:
                    CalendarView(showMenu: $showMenu)
                case .linksView:
                    LinksView(userId: viewModel.currentUserId, showMenu: $showMenu)
                case .profileView:
                    ProfileView(showMenu: $showMenu)
                case .adminView:
                    AdminView(showMenu: $showMenu)
                }
            }
            .disabled(showMenu)
            .overlay(
                Group {
                    if showMenu {
                        Color.black.opacity(0.35)
                            .ignoresSafeArea()
                            .onTapGesture {
                                withAnimation {
                                    showMenu = false
                                }
                            }
                    }
                }
            )
            if showMenu {
                SideMenuView(
                    selectedItem: $selectedItem,
                    showMenu: $showMenu,
                    isAdmin: viewModel.isAdmin
                )
            }
        }
        .onReceive(
            NotificationCenter.default.publisher(
                for: .notificationTapped
            )
        ) { notification in
            guard let userInfo = notification.userInfo,
                  let screen = userInfo["screen"] as? String else {
                return
            }
            
            switch screen {
            case "home":
                selectedItem = .homeView
            case "calendar":
                selectedItem = .calendarView
            case "todo":
                selectedItem = .todoView
            case "links":
                selectedItem = .linksView
            case "profile":
                selectedItem = .profileView
            case "admin":
                selectedItem = .adminView
            
            default:
                break
            }
            
        }
    }
}

struct KeyboardWarmupView: View {
    @State private var text = ""

    var body: some View {
        TextField("", text: $text)
            .disabled(true)
            .opacity(0)
            .onAppear {
                // Triggers keyboard system initialization
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                    let _ = text
                }
            }
    }
}

struct MyView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(selectedItem: .homeView)
            .environmentObject(ContentViewViewModel())
    }
}
