import SwiftUI

struct ToDoListItemView: View {
    @StateObject var viewModel = ToDoListItemViewViewModel()
    let item: ToDoListItem
    
    var body: some View {
        HStack(spacing: 0) {
            Button {
                viewModel.toggleCheck(item: item)
            } label: {
                Image(systemName: item.isDone ? "checkmark.square.fill": "square")
                    .font(.system(size: 32))
                    .foregroundColor(Color.navy)
            }
            Text(item.title)
                .font(.system(size: 15))
                .bold()
                .padding(.horizontal, 10)
            Spacer()
            Text("\(Date(timeIntervalSince1970: item.dueDate).formatted(.dateTime.month(.wide).day().hour().minute()))")
                .font(.system(size: 15))
                .padding(.horizontal, 20)
            if item.pinned && !item.isDone {
                Image(systemName: "pin.fill")
                    .tint(.navy)
                    .padding(.trailing, 10)
            }
        }
    }
}
