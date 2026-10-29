import SwiftUI
import FirebaseFirestore

struct FilterRolesView: View {
    @Binding var selectedFilters: Set<String>
    @State private var expandedSections: Set<String> = []
    @State private var isExpanded: Bool = false
    @FirestoreQuery(collectionPath: "roles") private var roles: [RoleItem]
    @FirestoreQuery(collectionPath: "users") private var users: [User]
        
    var body: some View {
        ZStack(alignment: .leading) {
            Color.blue2
                .frame(width: 230)
                .ignoresSafeArea(.all)
            
            VStack(alignment: .leading, spacing: 0) {
                Color.blue2
                    .frame(width: 230, height: 50)
                Color.navy
                    .frame(width: 230, height: 20)
                ZStack {
                    Color.white
                        .frame(width: 230, height: 40)
                    Text("Filters")
                        .font(.custom("ARIAL", size: 20))
                        .foregroundColor(.navy)
                }
                Color.navy
                    .frame(width: 230, height: 20)
                Spacer()
            }
            
            VStack(alignment: .leading, spacing: 0) {
                Spacer().frame(height: 150)
                VStack(spacing: 0) {
                    Button {
                        isExpanded.toggle()
                    } label: {
                        ZStack(alignment: .leading) {
                            Rectangle()
                                .fill(Color.navy)
                                .frame(width: 170, height: 45)
                                .clipShape(
                                    UnevenRoundedRectangle(
                                        topLeadingRadius: 0,
                                        bottomLeadingRadius: 0,
                                        bottomTrailingRadius: 20,
                                        topTrailingRadius: 20
                                    )
                                )
                            HStack(spacing: 10) {
                                Text("Role Types")
                                    .font(.custom("ARIAL", size: 18))
                                    .foregroundColor(.white)

                                Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                                    .foregroundColor(.white)

                                Spacer()
                            }
                            .padding(.horizontal, 10)
                            .frame(height: 45)
                        }
                    }
                    .padding(.bottom, 20)
                    
                    if isExpanded {
                        let roleTypes = Array(Set(roles.map(\.type))).sorted()
                        VStack(alignment: .leading, spacing: 0) {
                            ForEach(roleTypes, id: \.self) { type in
                                Button {
                                    if selectedFilters.contains(type) {
                                        selectedFilters.remove(type)
                                    } else {
                                        selectedFilters.insert(type)
                                    }
                                } label: {
                                    HStack(spacing: 0) {
                                        Image(systemName: selectedFilters.contains(type) ? "checkmark.square.fill":"square")
                                            .font(.system(size: 20))
                                            .foregroundColor(.white)
                                            .padding(.trailing, 10)

                                        Text(type.formattedString())
                                            .font(.custom("ARIAL", size: 20))
                                            .foregroundColor(.white)
                                            .bold()
                                        }
                                        .padding(.leading, 10)
                                    }
                                .padding(.bottom, 20)
                            }
                        }
                    }
                }
                .frame(width: 230)
                Color.navy
                    .frame(width: 230, height: 20)
                Spacer()
            }
            .ignoresSafeArea(.all)
        }
        .ignoresSafeArea(.all)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
