import SwiftUI
import FirebaseStorage

struct AddLinkView: View {
    @StateObject var viewModel = AddLinkViewViewModel()
    @FocusState private var isTyping: Bool
    @State private var selectedRoles: Set<RoleItem> = []
    
    public var body: some View {
        ZStack {
            VStack(spacing: 0) {
                Color.clear
                    .contentShape(Rectangle())
                    .frame(height: 150)
                    .padding(.leading, 60)
                    .onTapGesture {
                        isTyping = false
                    }
                Spacer()
            }
            Form {
                Section(header:
                    VStack(spacing: 0) {
                        Spacer().frame(height: 50)
                    }
                ) {
                    TextField("Name", text: $viewModel.name, axis: .vertical)
                        .textFieldStyle(OutlinedTextFieldStyle())
                        .background(Color.white)
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                        .focused($isTyping)
                        .listRowInsets(EdgeInsets(top: 4, leading: 16, bottom: 0, trailing: 16))
                        .padding(.vertical, 10)
                    TextField("Link", text: $viewModel.link, axis: .vertical)
                        .textFieldStyle(OutlinedTextFieldStyle())
                        .background(Color.white)
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                        .focused($isTyping)
                        .listRowInsets(EdgeInsets(top: 0, leading: 16, bottom: 4, trailing: 16))
                        .padding(.bottom, 10)
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Select Roles:")
                        Button {
                            viewModel.selectRolesView = true
                        } label: {
                            buttonLabel(title: "Select Roles")
                                .contentShape(Rectangle())
                        }
                        .listRowInsets(EdgeInsets(top: 0, leading: 16, bottom: 0, trailing: 16))
                        .frame(width: 150)
                        .buttonStyle(.borderless)
                        if !selectedRoles.isEmpty {
                            VStack(alignment: .leading, spacing: 10) {
                                Text("Selected Roles:")
                                WrappingHStack(horizontalSpacing: 8, verticalSpacing: 10) {
                                    ForEach(Array(selectedRoles)) { role in
                                        HStack(spacing: 6) {
                                            Text(role.name.formattedString())
                                            Button {
                                                selectedRoles.remove(role)
                                            } label: {
                                                Image(systemName: "xmark")
                                                    .tint(.navy)
                                            }
                                            .buttonStyle(.borderless)
                                        }
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 8)
                                        .background(Color(.systemGray5))
                                        .clipShape(RoundedRectangle(cornerRadius: 20))
                                    }
                                }
                                .fixedSize(horizontal: false, vertical: true)
                            }
                            .padding(.top, 10)
                        }
                    }
                    TLButton(title: "Publish", background: Color.navy) {
                        isTyping = false
                        let roleNames = selectedRoles.map{ $0.name }
                        viewModel.tags = roleNames
                        viewModel.addLink { success in
                            if success {
                                viewModel.activeAlert = .success
                                selectedRoles = []
                            } else {
                                viewModel.activeAlert = .error
                            }
                        }
                    }
                    .fixedSize(horizontal: false, vertical: true)
                }
                .listRowSeparator(.hidden)
            }
            .sheet(isPresented: $viewModel.selectRolesView) {
                SelectRolesView(selected: $selectedRoles)
            }
            .alert(item: $viewModel.activeAlert) { alert in
                switch alert {
                case .error:
                    return Alert(
                        title: Text("Error"),
                        message: Text(viewModel.errorMessage)
                    )
                case .success:
                    return Alert(
                        title: Text("Success"),
                        message: Text("Link saved successfully."),
                        dismissButton: .default(Text("OK")) {
                            isTyping = false
                        }
                    )
                }
            }
            .padding(.top, 150)
            .padding(.bottom, 150)
            .ignoresSafeArea(.container, edges: [.top, .bottom])
            .ignoresSafeArea(.keyboard)
        }
    }
}

private func buttonLabel(title: String) -> some View {
    ZStack {
        RoundedRectangle(cornerRadius: 10)
            .foregroundColor(.navy)

        HStack(spacing: 10) {
            Text(title)
                .foregroundColor(.white)
                .font(.system(size: 15))
                .bold()

            Image(systemName: "plus")
                .foregroundColor(.white)
        }
    }
    .frame(height: 45)
}
