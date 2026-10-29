import SwiftUI

struct FullscreenImageView: View {
    let imageURL: String
    @Environment(\.dismiss) private var dismiss
    
    public var body: some View {
        ZStack {
            Color.black
                .ignoresSafeArea()
            
            AsyncImage(url: URL(string: imageURL)) { image in
                image
                    .resizable()
                    .scaledToFill()
            } placeholder: {
                ProgressView()
            }
            VStack {
                HStack {
                    Spacer()
                    Button {
                        dismiss()
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .font(.largeTitle)
                            .foregroundColor(.white)
                    }
                    .padding()
                }
                Spacer()
            }
        }
    }
}
