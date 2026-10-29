import SwiftUI
import FirebaseFirestore

public struct HomeView: View {
    private let userId: String
    @Binding var showMenu: Bool
    @StateObject private var viewModel = HomeViewViewModel()
    @EnvironmentObject private var adminModel: ContentViewViewModel
    @State private var searchText = ""
    @State private var selectedArticle: Article?
    @State private var articleToDelete: Article?
    
    
    let db = Firestore.firestore()
    
    init(userId: String, showMenu: Binding<Bool>) {
        self.userId = userId
        self._showMenu = showMenu
    }
    
    private var filteredArticles: [Article] {
        if searchText.isEmpty {
                return viewModel.articles
        }

        return viewModel.articles.filter {
            $0.title.localizedCaseInsensitiveContains(searchText) ||
            $0.summary.localizedCaseInsensitiveContains(searchText) ||
            $0.tags.contains(searchText) ||
            $0.publisher.localizedCaseInsensitiveContains(searchText)
        }
    }
    
    public var body: some View {
        NavigationView {
            ZStack {
                DefaultView(title: "Home", showMenu: $showMenu, showMenuButton: true)
                VStack(spacing: 0) {
                    List(filteredArticles, id: \.id) { article in
                        Button {
                            selectedArticle = article
                        } label: {
                            VStack(alignment: .leading, spacing: 4) {
                                HStack {
                                    Text(article.title)
                                        .font(.headline)
                                    Spacer()
                                    
                                    /*if !article.firstTapped {
                                        ZStack {
                                            Color.darkRed
                                                .clipShape(RoundedRectangle(cornerRadius: 15))
                                                .frame(width: 45, height: 25)
                                            Text("New")
                                                .foregroundColor(Color.white)
                                                .font(.custom("ARIAL.TTF", size: 15))
                                        }
                                    }*/
                                    
                                    if article.pinned {
                                        Image(systemName: "pin.fill")
                                            .tint(.navy)
                                            .padding(.trailing, 10)
                                    }
                                    
                                    
                                    Image(systemName: "chevron.right")
                                        .foregroundColor(.gray)
                                }
                                Text(article.summary)
                                    .font(.subheadline)
                                    .lineLimit(1)
                            }
                        }
                        .buttonStyle(.plain)
                        .swipeActions(allowsFullSwipe: false) {
                            if adminModel.isAdmin {
                                Button {
                                    articleToDelete = article
                                } label: {
                                    Label("Delete", systemImage: "trash")
                                }
                                .tint(.darkRed)
                                Button {
                                    viewModel.togglePin(article: article)
                                } label: {
                                    Label(article.pinned ? "Unpin":"Pin", systemImage: article.pinned ? "pin.slash":"pin")
                                        .tint(.navy)
                                }
                            }
                        }
                    }
                    .padding(.top, 150)
                    .padding(.bottom, 150)
                    .ignoresSafeArea(.container, edges: [.top, .bottom])
                    .searchable(text: $searchText, prompt: "Search Events")
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled(true)
                    .onAppear {
                        viewModel.startListeningForUser(userId: userId)
                    }
                    .alert("Delete Article?", isPresented: Binding(
                                get: { articleToDelete != nil },
                                set: { if !$0 { articleToDelete = nil } }
                           )) {

                        Button("Delete", role: .destructive) {
                            if let article = articleToDelete {
                                viewModel.deleteArticle(articleId: article.id ?? "")
                            }
                            articleToDelete = nil
                        }

                        Button("Cancel", role: .cancel) {
                            articleToDelete = nil
                        }
                    } message: {
                        Text("This action cannot be undone.")
                    }
                }
            }
            .ignoresSafeArea(.keyboard)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(.white)
            .sheet(item: $selectedArticle) { article in
                ArticleView(article: article)
            }
        }
    }
}
