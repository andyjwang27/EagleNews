import SwiftUI

public struct RoleChangeView: View{
    @Binding var showMenu: Bool
    @State var mode: AssignDropdownView = .none
    
    public var body: some View {
        ZStack {
            DefaultView(title: "Change Roles", showMenu: $showMenu, showMenuButton: true)
            Group {
                switch mode {
                case .assign:
                    AssignRolesView(showMenu: $showMenu)
                case .remove:
                    RemoveRolesView(showMenu: $showMenu)
                case .none:
                    Color(.systemGroupedBackground)
                        .padding(.top, 150)
                        .padding(.bottom, 150)
                        .ignoresSafeArea(.container, edges: [.top, .bottom])
                }
            }
            VStack(alignment: .leading, spacing: 0) {
                Spacer().frame(height: 168)
                HStack(spacing: 0) {
                    AssignButton(mode: $mode)
                    Spacer()
                }
                Spacer()
            }
            .ignoresSafeArea(.container, edges: [.top, .bottom])
        }
    }
}
