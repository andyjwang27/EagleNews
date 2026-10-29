import SwiftUI

struct NewItemView: View {
    @StateObject var viewModel = NewItemViewViewModel()
    @FocusState private var isTyping: Bool
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        ZStack {
            DefaultView(title: "New Item", showMenu: .constant(false), showMenuButton: false){
                RoundButton(imageName: "xmark", background: .navy) {
                    dismiss()
                }
            }
            VStack(spacing: 0) {
                Color.clear
                    .contentShape(Rectangle())
                    .frame(height: 150)
                    .padding(.trailing, 60)
                    .onTapGesture {
                        isTyping = false
                    }
                Spacer()
            }
            Form {
                Section {
                    TextField("Title", text: $viewModel.title, axis: .vertical)
                        .textFieldStyle(OutlinedTextFieldStyle())
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                        .focused($isTyping)
                    DatePicker("Due Date", selection: $viewModel.dueDate)
                        .datePickerStyle(.compact)
                    TLButton(title: "Save", background: .navy) {
                        isTyping = false
                        
                        viewModel.save { success in
                            if success {
                                viewModel.activeAlert = .success
                            } else {
                                viewModel.activeAlert = .error
                            }
                        }
                    }
                }
                .listRowSeparator(.hidden)
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
                        message: Text("Item saved successfully."),
                        dismissButton: .default(Text("OK")) {
                            isTyping = false
                            dismiss()
                        }
                    )
                }
            }
            .padding(.top, 150)
            .padding(.bottom, 150)
            .ignoresSafeArea(.container, edges: [.top, .bottom])
        }
        .ignoresSafeArea(.keyboard)
    }
}
