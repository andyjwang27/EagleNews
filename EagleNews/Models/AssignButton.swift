import SwiftUI

enum AssignDropdownView {
    case none
    case assign
    case remove
}

struct AssignButton: View {
    @State private var expanded = false
    @Binding var mode: AssignDropdownView
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Button {
                expanded.toggle()
            } label: {
                HStack {
                    Text("Select")
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
                        mode = .assign
                        expanded = false
                    } label: {
                        Label("Assign", systemImage: "hand.tap")
                            .tint(.navy)
                    }
                    Button {
                        mode = .remove
                        expanded = false
                    } label: {
                        Label("Delete", systemImage: "eraser.badge.xmark")
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
