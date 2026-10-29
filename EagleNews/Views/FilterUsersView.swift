import SwiftUI
import FirebaseFirestore

struct FilterUsersView: View {
    @Binding var selectedFilters: Set<String>
    @State private var expandedSections: Set<String> = []
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
                    FilterSectionView(
                        title: "Grades",
                        roles: roles.filter { $0.type == "gradeLevel" },
                        expandedSections: $expandedSections, selectedFilters: $selectedFilters
                    )
                    .padding(.bottom, 20)
                    FilterSectionView(
                        title: "Clubs",
                        roles: roles.filter { $0.type == "club" },
                        expandedSections: $expandedSections, selectedFilters: $selectedFilters
                    )
                    .padding(.bottom, 20)

                    FilterSectionView(
                        title: "Sports",
                        roles: roles.filter { $0.type == "sports" },
                        expandedSections: $expandedSections, selectedFilters: $selectedFilters
                    )
                    .padding(.bottom, 20)

                    FilterSectionView(
                        title: "Priority",
                        roles: roles.filter { $0.type == "priorityLevel" },
                        expandedSections: $expandedSections, selectedFilters: $selectedFilters
                    )
                    .padding(.bottom, 20)
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
