import SwiftUI

struct RoundButton: View {
    let imageName: String
    let background: Color
    let action: () -> Void
    
    var body: some View {
        Button {
            action()
        } label: {
            Image(systemName: "\(imageName)")
                .font(.system(size: 22))
                .frame(width: 22, height: 22)
                .foregroundColor(Color.white)
                .padding()
                .background(background)
                .clipShape(Circle())
                .shadow(radius: 4)
        }
        .buttonStyle(.borderless)
        .frame(width: 50, height: 50)
    }
}
