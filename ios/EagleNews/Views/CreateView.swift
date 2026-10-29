import SwiftUI

public struct CreateView: View{
    @Binding var showMenu: Bool
    @State var mode: CreateDropdownView = .none
    
    public var body: some View {
        ZStack {
            DefaultView(title: "Create", showMenu: $showMenu, showMenuButton: true)
            Group {
                switch mode {
                case .article:
                    AddArticleView()
                case .link:
                    AddLinkView()
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
                    CreateButton(mode: $mode)
                    Spacer()
                }
                Spacer()
            }
            .ignoresSafeArea(.container, edges: [.top, .bottom])

        }
        
    }
}
