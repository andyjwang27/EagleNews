import SwiftUI
import FirebaseFirestore

struct AdminInfoView: View {
    @Binding var showMenu: Bool
    @State private var searchText = ""
    @FocusState var isTyping: Bool
    @FirestoreQuery(collectionPath: "roles") var roles: [RoleItem]
    
    init(showMenu: Binding<Bool>, isSheet: Bool) {
        self._showMenu = showMenu
    }
        
    private var filteredRoles: [RoleItem] {
        if searchText.isEmpty {
            return roles
        } else {
            return roles.filter { role in
                role.name.localizedCaseInsensitiveContains(searchText)
            }
        }
    }
    
    public var body: some View {
        ZStack {
            DefaultView(title: "Admin Info", showMenu: $showMenu, showMenuButton: true)
            Form {
                TextField("Search Roles", text: $searchText, axis: .vertical)
                    .textFieldStyle(OutlinedTextFieldStyle())
                    .background(Color.white)
                    .autocorrectionDisabled()
                    .textInputAutocapitalization(.never)
                    .focused($isTyping)
                    .frame(height: 45)
                    .padding(.trailing, 10)
                    .padding(.leading, 10)
                List {
                    Section(header: Text("Available Roles: \(filteredRoles.count)")) {
                        ForEach(filteredRoles, id: \.self) { role in
                            Text(role.name.formattedString())
                        }
                    }
                }
            }
            .padding(.top, 150)
            .padding(.bottom, 150)
            .ignoresSafeArea(.container, edges: [.top, .bottom])
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
        }
        .ignoresSafeArea(.keyboard)
    }
    
}
