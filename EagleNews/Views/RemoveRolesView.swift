import SwiftUI
import FirebaseAuth

public struct RemoveRolesView: View {
    @Binding var showMenu: Bool
    @State private var selectedUser: User?
    @State private var selectedRoles: Set<RoleItem> = []
    @State private var activeSheet: AssignSheet?
    @StateObject private var viewModel = RemoveRolesViewViewModel()
    let currentUserId = Auth.auth().currentUser?.uid

    public var body: some View {
        ZStack {
            Form {
                Section(header: Spacer().frame(height: 50)) {
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Select User:")
                        Button {
                            activeSheet = .selectUsers
                        } label: {
                            buttonLabel(title: "Select Users")
                                .contentShape(Rectangle())
                        }
                        .buttonStyle(.borderless)
                        .frame(width: 150)
                        .listRowInsets(EdgeInsets(top: 0, leading: 16, bottom: 0, trailing: 16))
                        if let selectedUser {
                            VStack(alignment: .leading, spacing: 10) {
                                Text("Selected User:")
                                HStack(spacing: 0) {
                                    HStack(spacing: 6) {
                                        if selectedUser.id == currentUserId{
                                            HStack(spacing: 10) {
                                                Text("\(selectedUser.name.formattedString()) (You)")
                                                    .lineLimit(2)
                                                    .minimumScaleFactor(0.8)
                                                    .foregroundColor(.navy)
                                                    .font(.system(size: 15))
                                            }
                                        } else {
                                            Text(selectedUser.name.formattedString())
                                                .lineLimit(2)
                                                .minimumScaleFactor(0.8)
                                                .foregroundColor(.navy)
                                                .font(.system(size: 15))
                                        }
                                    }
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 8)
                                    .background(Color(.systemGray5))
                                    .clipShape(RoundedRectangle(cornerRadius: 20))
                                    Spacer()
                                }
                            }
                            .padding(.top, 10)
                        }
                    }
                    if let _ = selectedUser {
                        VStack(alignment: .leading, spacing: 10) {
                            Text("Select Roles to Delete:")
                            Button {
                                activeSheet = .selectRoles
                            } label: {
                                buttonLabel(title: "Select Roles")
                                    .contentShape(Rectangle())
                            }
                            .listRowInsets(EdgeInsets(top: 0, leading: 16, bottom: 0, trailing: 16))
                            .frame(width: 150)
                            .buttonStyle(.borderless)
                            selectedRolesChips
                        }
                        TLButton(title: "Remove", background: .navy) {
                            guard let user = selectedUser else {
                                return
                            }
                            
                            viewModel.removeRoles(from: user, rolesToDelete: selectedRoles.map { $0.name })
                        }
                    }
                }
                .listRowSeparator(.hidden)
            }
            .sheet(item: $activeSheet) { sheet in
                switch sheet {
                case .selectUsers:
                    SingleSelectUserView(selected: $selectedUser)
                case .selectRoles:
                    SelectUserRolesView(selected: $selectedRoles, selectedUser: $selectedUser)
                }
            }
            .onChange(of: selectedUser) { _, _ in
                selectedRoles.removeAll()
            }
            .onChange(of: viewModel.didSucceed) { oldValue, newValue in
                guard newValue else { return }

                selectedUser = nil
                selectedRoles.removeAll()

                viewModel.didSucceed = false
            }
            .alert(item: $viewModel.activeAlert) { alert in
                switch alert {
                case .success:
                    return Alert(
                        title: Text("Success"),
                        message: Text(viewModel.successMessage),
                        dismissButton: .default(Text("OK"))
                    )
                    
                case .error:
                    return Alert(
                        title: Text("Error"),
                        message: Text(viewModel.errorMessage),
                        dismissButton: .default(Text("OK"))
                    )
                }
            }
            .padding(.top, 150)
            .padding(.bottom, 150)
            .ignoresSafeArea(.container, edges: [.top, .bottom])
            .ignoresSafeArea(.keyboard)
        }
    }
    @ViewBuilder
    private var selectedRolesChips: some View {
        if !selectedRoles.isEmpty {
            VStack(alignment: .leading, spacing: 10) {
                Text("Selected Roles:")

                WrappingHStack(horizontalSpacing: 8, verticalSpacing: 10) {
                    ForEach(Array(selectedRoles)) { role in
                        roleChip(role)
                    }
                }
                .fixedSize(horizontal: false, vertical: true)
            }
            .padding(.top, 10)
        }
    }

    private func roleChip(_ role: RoleItem) -> some View {
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
