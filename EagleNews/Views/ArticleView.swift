import SwiftUI

struct ArticleView: View {
    let article: Article
    @State private var selectedImageURL: String?
    @State private var showImageViewer = false
    @Environment(\.dismiss) private var dismiss
    
    public var body: some View {
        NavigationView {
            ZStack {
                DefaultView(title: article.title, showMenu: .constant(false), showMenuButton: false) {
                    RoundButton(imageName: "xmark", background: .navy) {
                        dismiss()
                    }
                }
                VStack(alignment: .leading, spacing: 0) {
                    Spacer().frame(height: 150)
                    Form {
                        Text(article.summary)
                        if !article.imageURLs.isEmpty {
                            ScrollView(.horizontal) {
                                HStack(spacing: 10) {
                                    ForEach(article.imageURLs, id: \.self) { urlString in
                                        AsyncImage(url: URL(string: urlString)) { image in
                                            image
                                                .resizable()
                                                .scaledToFit()
                                                .frame(width: 250, height: 200)
                                                .clipped()
                                                .onTapGesture {
                                                    selectedImageURL = urlString
                                                    showImageViewer = true
                                                }
                                        } placeholder: {
                                            ProgressView()
                                                .frame(width: 250, height: 200)
                                        }
                                    }
                                }
                            }
                        }
                        Text("Published on \(Date(timeIntervalSince1970: article.publishedAt).formatted(date: .abbreviated, time: .shortened)) by \(article.publisher)")

                    }
                    .background(Color.white)
                    .scrollContentBackground(.hidden)
                    .sheet(isPresented: $showImageViewer) {
                        if let selectedImageURL {
                            FullscreenImageView(imageURL: selectedImageURL)
                        }
                    }
                    Spacer().frame(height: 150)
                }
            }
        }
    }
    
}
