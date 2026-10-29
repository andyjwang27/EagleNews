import SwiftUI
import FirebaseFirestore
import FirebaseAuth

struct SelectUsersView: View {
    @FirestoreQuery(collectionPath: "users") var users: [User]
    @Binding var selected: Set<User>
    @FocusState var isTyping: Bool
    @State private var showFilter: Bool = false
    @State private var selectedFilters: Set<String> = []
    @State private var searchText: String = ""
    @Environment(\.dismiss) private var dismiss
    let currentUserId = Auth.auth().currentUser?.uid
    
    private var filteredUsers: [User] {
        users.filter { user in
            let matchesRole = selectedFilters.isEmpty || user.activeRoles.contains {
                selectedFilters.contains($0)
            }
            
            let matchesSearch = searchText.isEmpty || user.name.localizedCaseInsensitiveContains(searchText)
            
            return matchesRole && matchesSearch
        }
    }
    
    var body: some View {
        ZStack {
            DefaultView(title: "Select Users", showMenu: .constant(false), showMenuButton: false) {
                RoundButton(imageName: "xmark", background: .navy) {
                    dismiss()
                }
            }
            VStack(spacing: 0) {
                Color.clear
                    .contentShape(Rectangle())
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .onTapGesture {
                        isTyping = false
                    }
                Spacer()
            }
            .padding(.vertical, 150)
            Form {
                Section("Filters") {
                    HStack(spacing: 0) {
                        TextField("Search Users", text: $searchText, axis: .vertical)
                            .textFieldStyle(OutlinedTextFieldStyle())
                            .background(Color.white)
                            .autocorrectionDisabled()
                            .textInputAutocapitalization(.never)
                            .focused($isTyping)
                            .frame(height: 45)
                            .padding(.trailing, 10)
                            .padding(.leading, 10)
                        Button {
                            showFilter.toggle()
                        } label: {
                            ZStack {
                                RoundedRectangle(cornerRadius: 10)
                                    .foregroundColor(.navy)
                                    .frame(height: 45)
                                HStack(spacing: 10) {
                                    Text("Select Filters")
                                        .foregroundColor(Color.white)
                                        .font(.system(size: 15))
                                        .bold()
                                    Image(systemName: "plus")
                                        .frame(width: 25, height: 25)
                                        .tint(Color.white)
                                }
                            }
                            .frame(width: 150)
                        }
                        .buttonStyle(.borderless)
                        .padding(.trailing, 10)
                    }
                    if !selectedFilters.isEmpty {
                        WrappingHStack(horizontalSpacing: 8, verticalSpacing: 10) {
                            ForEach(Array(selectedFilters), id: \.self) { filter in
                                HStack(spacing: 6) {
                                    Text(filter.formattedString())
                                    Button {
                                        selectedFilters.remove(filter)
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
                }
                .listRowSeparator(.hidden)

                Section("Users") {
                    ScrollView {
                        WrappingHStack(horizontalSpacing: 8, verticalSpacing: 10) {
                            ForEach(filteredUsers, id: \.self) { user in
                                userButton(for: user)
                            }
                        }
                        .fixedSize(horizontal: false, vertical: true)
                    }
                }
            }
            .padding(.top, 150)
            .padding(.bottom, 150)
            .ignoresSafeArea(.container, edges: [.top, .bottom])
            .ignoresSafeArea(.keyboard)
        }
        .overlay {
            if showFilter {
                ZStack(alignment: .leading) {
                    Color.black.opacity(0.35)
                        .ignoresSafeArea()
                        .onTapGesture {
                            showFilter = false
                        }

                    FilterUsersView(selectedFilters: $selectedFilters)
                }
            }
        }
    }
    
    @ViewBuilder
    private func userButton(for user: User) -> some View {
        Button {
            toggleUser(user)
        } label: {
            userLabel(for: user)
        }
        .buttonStyle(.borderless)
    }

    private func toggleUser(_ user: User) {
        if selected.contains(user) {
            selected.remove(user)
        } else {
            selected.insert(user)
        }
    }

    @ViewBuilder
    private func userLabel(for user: User) -> some View {
        HStack {
            userNameView(for: user)

            selectionIcon(for: user)
        }
        .frame(height: 50)
        .padding(.horizontal, 10)
        .background(.ultraThinMaterial)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    private func userNameView(for user: User) -> some View {
        let displayName = user.id == currentUserId
            ? "\(user.name.formattedString()) (You)"
            : user.name.formattedString()

        return Text(displayName)
            .lineLimit(2)
            .minimumScaleFactor(0.8)
            .foregroundColor(.navy)
            .font(.system(size: 15))
    }

    private func selectionIcon(for user: User) -> some View {
        Image(
            systemName: selected.contains(user)
                ? "checkmark.circle.fill"
                : "circle"
        )
        .foregroundColor(.navy)
        .frame(width: 15, height: 15)
    }

}
