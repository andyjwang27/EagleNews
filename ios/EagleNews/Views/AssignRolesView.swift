import SwiftUI
import FirebaseAuth

enum AssignSheet: Identifiable {
    case selectUsers
    case selectRoles

    var id: String {
        switch self {
        case .selectUsers: return "selectUsers"
        case .selectRoles: return "selectRoles"
        }
    }
}

public struct AssignRolesView: View {
    @Binding var showMenu: Bool
    @State private var selectedUsers: Set<User> = []
    @State private var selectedRoles: Set<RoleItem> = []
    @State private var activeSheet: AssignSheet?
    @StateObject private var viewModel = AssignRolesViewViewModel()
    let currentUserId = Auth.auth().currentUser?.uid

    public var body: some View {
        ZStack {
            Form {
                Section(header: Spacer().frame(height: 50)) {
                    HStack(spacing: 12) {
                        Button {
                            activeSheet = .selectUsers
                        } label: {
                            buttonLabel(title: "Select Users")
                                .contentShape(Rectangle())
                        }
                        .buttonStyle(.borderless)
                        Spacer()
                        Button {
                            activeSheet = .selectRoles
                        } label: {
                            buttonLabel(title: "Select Roles")
                                .contentShape(Rectangle())
                        }
                        .buttonStyle(.borderless)
                    }
                    if !selectedUsers.isEmpty {
                        Text("Selected Users:")
                        WrappingHStack(horizontalSpacing: 8, verticalSpacing: 10) {
                            ForEach(Array(selectedUsers)) { user in
                                HStack(spacing: 6) {
                                    if user.id == currentUserId{
                                        HStack(spacing: 10) {
                                            Text("\(user.name.formattedString()) (You)")
                                                .lineLimit(2)
                                                .minimumScaleFactor(0.8)
                                                .foregroundColor(.navy)
                                                .font(.system(size: 15))
                                        }
                                    } else {
                                        Text(user.name.formattedString())
                                            .lineLimit(2)
                                            .minimumScaleFactor(0.8)
                                            .foregroundColor(.navy)
                                            .font(.system(size: 15))
                                    }
                                    Button {
                                        selectedUsers.remove(user)
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
                    if !selectedRoles.isEmpty {
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
                    TLButton(title: "Assign", background: .navy) {
                        let roleNames = selectedRoles.map{ $0.name }
                        viewModel.selectedRoles = roleNames
                        viewModel.selectedUsers = selectedUsers
                        viewModel.assign()
                    }
                }
                .listRowSeparator(.hidden)
            }
            .onChange(of: viewModel.didSucceed) { oldValue, newValue in
                guard newValue else { return }

                selectedUsers.removeAll()
                selectedRoles.removeAll()

                viewModel.didSucceed = false
            }
            .sheet(item: $activeSheet) { sheet in
                switch sheet {
                case .selectUsers:
                    SelectUsersView(selected: $selectedUsers)
                case .selectRoles:
                    SelectRolesView(selected: $selectedRoles)
                }
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
                        message: Text("Roles successfully assigned."),
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
