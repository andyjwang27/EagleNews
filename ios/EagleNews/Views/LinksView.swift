import SwiftUI

struct LinksView: View {
    @Binding var showMenu: Bool
    private let userId: String
    @StateObject private var viewModel = LinksViewViewModel()
    @EnvironmentObject private var adminModel: ContentViewViewModel
    @State private var selectedLink: Link?
    @State private var linkToDelete: Link?
    @Environment(\.openURL) private var openURL
    
    init(userId: String, showMenu: Binding<Bool>) {
        self.userId = userId
        self._showMenu = showMenu
    }
    
    public var body: some View {
        ZStack {
            DefaultView(title: "Links", showMenu: $showMenu, showMenuButton: true)
            VStack(spacing: 0) {
                List(viewModel.links) { link in
                    Button {
                        selectedLink = link
                        openURL(URL(string: link.link)!)
                    } label: {
                        VStack(alignment: .leading, spacing: 4) {
                            HStack {
                                Text(link.name)
                                    .font(.headline)
                                    .foregroundColor(.black)
                                Spacer()
                                
                                Image(systemName: "chevron.right")
                                    .foregroundColor(.gray)
                            }
                            Text(link.link)
                                .lineLimit(2)
                        }
                    }
                    .swipeActions(allowsFullSwipe: adminModel.isAdmin) {
                        if adminModel.isAdmin {
                            Button(role: .destructive) {
                                linkToDelete = link
                            } label: {
                                Label("Delete", systemImage: "trash")
                                    .tint(.darkRed)
                            }
                        }
                    }
                }
                .padding(.top, 150)
                .padding(.bottom, 150)
                .ignoresSafeArea(.container, edges: [.top, .bottom])
                .alert("Delete Link?", isPresented: Binding(
                    get:{linkToDelete != nil},
                    set:{ if $0 {linkToDelete = nil} }
                )) {
                    Button("Delete", role: .destructive) {
                        if let link = linkToDelete {
                            viewModel.deleteLink(link)
                        }
                        linkToDelete = nil
                    }
                    
                    Button("Cancel", role: .cancel) {
                        linkToDelete = nil
                    }
                } message: {
                    Text("This action cannot be undone.")
                }
            }
        }
        .onAppear {
            viewModel.startListeningForLinks(userId: userId)
        }
    }
    
}
