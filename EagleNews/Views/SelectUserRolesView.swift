import SwiftUI
import FirebaseFirestore
import FirebaseAuth

struct SelectUserRolesView: View {
    @FirestoreQuery(collectionPath: "roles") var roles: [RoleItem]
    @Binding var selected: Set<RoleItem>
    @FocusState var isTyping: Bool
    @State private var showFilter: Bool = false
    @State private var selectedFilters: Set<String> = []
    @State private var searchText: String = ""
    @Binding var selectedUser: User?
    @Environment(\.dismiss) private var dismiss
    let currentUserId = Auth.auth().currentUser?.uid
    
    private var userRoles: [RoleItem] {
        guard let selectedUser else {
            return []
        }
        
        return roles.filter { role in
            selectedUser.activeRoles.contains(role.name)
        }
    }
    
    private var filteredRoles: [RoleItem] {
        let baseRoles = userRoles
        
        if searchText.isEmpty {
            return baseRoles
        }
        return baseRoles.filter {
            $0.name.localizedCaseInsensitiveContains(searchText)
        }
    }
    
    var body: some View {
        ZStack {
            DefaultView(title: "Select Roles", showMenu: .constant(false), showMenuButton: false) {
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
                        TextField("Search Roles", text: $searchText, axis: .vertical)
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

                Section("Roles") {
                    ScrollView {
                        WrappingHStack(horizontalSpacing: 8, verticalSpacing: 10) {
                            ForEach(filteredRoles, id: \.self) { role in
                                RoleButton(
                                    role: role,
                                    isSelected: selected.contains(role)
                                ) {
                                    if selected.contains(role) {
                                        selected.remove(role)
                                    } else {
                                        selected.insert(role)
                                    }
                                }
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
                    FilterRolesView(selectedFilters: $selectedFilters)
                }
            }
        }
    }
}

