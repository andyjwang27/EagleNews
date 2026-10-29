import SwiftUI

struct FilterSectionView: View {
    let title: String
    let roles: [RoleItem]
    @Binding var expandedSections: Set<String>
    @Binding var selectedFilters: Set<String>
    
    private var isExpanded: Bool {
        expandedSections.contains(title)
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Button {
                if isExpanded {
                    expandedSections.remove(title)
                } else {
                    expandedSections.insert(title)
                }
            } label: {
                ZStack(alignment: .leading) {
                    Rectangle()
                        .fill(Color.navy)
                        .frame(width: 150, height: 45)
                        .clipShape(
                            UnevenRoundedRectangle(
                                topLeadingRadius: 0,
                                bottomLeadingRadius: 0,
                                bottomTrailingRadius: 20,
                                topTrailingRadius: 20
                            )
                        )
                    HStack(spacing: 10) {
                        Text(title)
                            .font(.custom("ARIAL", size: 20))
                            .foregroundColor(.white)

                        Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                            .foregroundColor(.white)

                        Spacer()
                    }
                    .padding(.horizontal, 10)
                    .frame(height: 45)
                }
            }
            
            if isExpanded {
                VStack(alignment: .leading, spacing: 8) {
                    ForEach(roles) { role in
                        HStack(spacing: 8) {
                            Button {
                                if selectedFilters.contains(role.name) {
                                    selectedFilters.remove(role.name)
                                } else {
                                    selectedFilters.insert(role.name)
                                }
                            } label: {
                                ZStack(alignment: .leading) {
                                    HStack(spacing: 0) {
                                        Image(systemName: selectedFilters.contains(role.name) ? "checkmark.square.fill": "square")
                                            .font(.system(size: 20))
                                            .foregroundColor(Color.white)
                                            .padding(.trailing, 10)
                                        Text(role.name.formattedString())
                                            .font(.custom("ARIAL", size: 20))
                                            .foregroundColor(.white)
                                            .bold()
                                    }
                                    .padding(.leading, 10)
                                }
                            }
                            Spacer()
                        }
                        
                    }
                }
                .padding(.leading, 20)
                .padding(.top, 10)
            }
        }
        .ignoresSafeArea(.all)
    }
}
