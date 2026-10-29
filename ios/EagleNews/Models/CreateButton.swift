import SwiftUI

enum CreateDropdownView {
    case none
    case article
    case link
}

struct CreateButton: View {
    @State private var expanded = false
    @Binding var mode: CreateDropdownView
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Button {
                expanded.toggle()
            } label: {
                HStack {
                    Text("Create")
                        .foregroundColor(Color.white)
                        .padding(.trailing, 10)
                    Image(systemName: expanded ? "chevron.up" : "chevron.down")
                        .foregroundColor(Color.white)
                }
                .padding(.all, 10)
                .background(Color.navy)
                .cornerRadius(12)
            }
            .buttonStyle(.plain)
            .frame(width: 125, height: 25)
            .padding(.bottom, 15)

            if expanded {
                VStack(alignment: .leading, spacing: 12) {
                    Button {
                        mode = .article
                        expanded = false
                    } label: {
                        Label("Article", systemImage: "person.badge.key.fill")
                            .tint(.navy)
                    }
                    Button {
                        mode = .link
                        expanded = false
                    } label: {
                        Label("Link", systemImage: "link")
                            .tint(.navy)
                    }
                }
                .padding()
                .background(.ultraThinMaterial)
                .clipShape(RoundedRectangle(cornerRadius: 16))
                .shadow(radius: 8)
                .padding(.leading, 10)
                .transition(.opacity.combined(with: .move(edge: .top)))
            }
        }
    }
}
