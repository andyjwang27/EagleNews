import SwiftUI

struct EditToDoView: View {
    @Binding var showMenu: Bool
    @StateObject var viewModel: EditToDoViewViewModel
    @FocusState var isTyping: Bool
    @Environment(\.dismiss) private var dismiss
    let selectedToDo: ToDoListItem
    
    init(selectedToDo: ToDoListItem, showMenu: Binding<Bool>) {
        _viewModel = StateObject(wrappedValue: EditToDoViewViewModel(selectedToDo: selectedToDo))
        self._showMenu = showMenu
        self.selectedToDo = selectedToDo
    }
    
    var body: some View {
        ZStack {
            DefaultView(title: "Edit To-Do", showMenu: .constant(false), showMenuButton: false) {
                RoundButton(imageName: "xmark", background: .navy) {
                    dismiss()
                }
            }
            VStack(spacing: 0) {
                Color.clear
                    .frame(height: 150)
                    .contentShape(Rectangle())
                    .padding(.trailing, 60)
                    .onTapGesture {
                        isTyping = false
                    }
                Spacer()
            }
            Form {
                TextField("Title", text: $viewModel.title, axis: .vertical)
                    .textFieldStyle(OutlinedTextFieldStyle())
                    .background(Color.white)
                    .autocorrectionDisabled()
                    .textInputAutocapitalization(.never)
                    .focused($isTyping)
                DatePicker("Due Date", selection: $viewModel.dueDate)
                    .datePickerStyle(.compact)
                TLButton(title: "Save", background: .navy) {
                    isTyping = false
                    
                    viewModel.update { success in
                        if success {
                            viewModel.activeAlert = .success
                        } else {
                            viewModel.activeAlert = .error
                        }
                    }
                }
            }
            .alert(item: $viewModel.activeAlert) { alert in
                switch alert {
                case .error:
                    return Alert(
                        title: Text("Error"),
                        message: Text(viewModel.errorMessage),
                        dismissButton: .default(Text("OK")) {
                            isTyping = false
                        }
                    )
                case .success:
                    return Alert(
                        title: Text("Success"),
                        message: Text("Changes saved successfully."),
                        dismissButton: .default(Text("OK")) {
                            isTyping = false
                            dismiss()
                        }
                    )
                }
            }
            .padding(.top, 150)
            .padding(.bottom, 150)
        }
        .ignoresSafeArea(.container, edges: [.top, .bottom])
        .ignoresSafeArea(.keyboard)
    }
}
