import SwiftUI
import Foundation

struct SideMenuView: View {
    @Binding var selectedItem: TabIcon
    @Binding var showMenu: Bool
    @State var isAnimation = false
    let isAdmin: Bool
    let minDrag: CGFloat = 100
    @StateObject var viewModel = SideMenuViewViewModel()
    @StateObject var testAdminModel = ContentViewViewModel()
    private let rowHeight: CGFloat = 70
    private let menuTopOffset: CGFloat = 0
    
    private var yOffset: CGFloat {
        CGFloat(selectedItem.menuIndex) * 70
    }
    
    var body: some View {
        ZStack(alignment: .leading) {
            // Background Rectangle
            Rectangle()
                .fill(Color.blue2)
                .frame(maxHeight: .infinity)
                .frame(width: 265)
                .ignoresSafeArea(.all)

            // Info
            VStack(alignment: .leading, spacing: 0) {
                // Top Rectangle
                Color.blue2
                    .frame(width: 265, height: 50)
                Color.navy
                    .frame(width: 265, height: 20)
                // Logo + Welcome Line
                ZStack {
                    Color.white
                        .frame(width: 265, height: 90)
                    if let user = viewModel.user {
                        HStack {
                            Image("logoOfficial").resizable()
                                .frame(width: 70, height: 70)
                                .mask(Circle())
                                .shadow(color: .gray.opacity(0.5), radius: 3, x: 0, y: 0)
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Eagle News")
                                    .frame(width: 120, height: 15, alignment: .center)
                                    .font(.custom("ARIAL", size: 18))
                                    .bold()
                                    .foregroundColor(Color.navy)
                                    .padding(.bottom, 2)
                                    .underline()
                                Text("Welcome")
                                    .frame(width: 120, height: 15, alignment: .center)
                                    .opacity(0.5)
                                    .font(.custom("ARIAL", size: 15))
                                    .bold()
                                    .foregroundColor(Color.navy)
                                    .padding(.bottom, 2)
                                Text("\(user.name)!")
                                    .frame(width: 120, height: 15, alignment: .center)
                                    .opacity(0.5)
                                    .font(.custom("ARIAL", size: 15))
                                    .bold()
                                    .foregroundColor(Color.navy)
                            }
                        }
                        .foregroundColor(Color.white)
                        .padding(.horizontal, 20)
                    } else {
                        // If the user hasn't loaded, just say "Welcome"
                        HStack {
                            Image("logoOfficial").resizable()
                                .frame(width: 70, height: 70)
                                .mask(Circle())
                                .shadow(color: .gray.opacity(0.5), radius: 3, x: 0, y: 0)
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Eagle News")
                                    .frame(width: 120, height: 15, alignment: .center)
                                    .font(.custom("ARIAL", size: 15))
                                    .padding(.bottom, 2)
                                    .underline()
                                Text("Welcome")
                                    .frame(width: 120, height: 15, alignment: .center)
                                    .opacity(0.5)
                                    .font(.custom("ARIAL", size: 13))
                                    .padding(.bottom, 2)
                                Text("")
                                    .frame(width: 120, height: 15, alignment: .center)
                                    .opacity(0.5)
                                    .font(.custom("ARIAL", size: 13))
                            }
                        }
                        .foregroundColor(Color.white)
                        .padding(.horizontal, 20)
                    }
                }
                .onAppear {
                    viewModel.fetchUser()
                }
                
                // Bottom Rectangle
                Color.navy
                    .frame(width: 265, height: 20)
                Spacer()
            }
            .ignoresSafeArea(.all)

            // Links
            ZStack(alignment: .leading) {
                // Background Rectangle
                VStack(spacing: 0) {
                    Spacer().frame(height: 210)
                    Color.navy
                        .frame(width: isAnimation ? 7 : 230, height: 60)
                        .cornerRadius(10)
                        .padding(.horizontal, 5)
                        .offset(x: -10)
                        .offset(y: yOffset)
                        .animation(.default, value: selectedItem)
                        .frame(maxHeight: 60, alignment: .top)
                    Spacer()
                }
                .ignoresSafeArea(.all)
                
                VStack(spacing: 0) {
                    Spacer().frame(height: 205)
                    VStack(spacing: 0) {
                        ForEach(sideBarItems(isAdmin: isAdmin)) { item in
                            Button {
                                withAnimation {
                                    isAnimation = true
                                }
                                
                                DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                                    withAnimation {
                                        selectedItem = item.tab
                                    }
                                }
                                
                                DispatchQueue.main.asyncAfter(deadline: .now() + 0.6) {
                                    withAnimation {
                                        isAnimation = false
                                        showMenu = false
                                    }
                                }
                            } label: {
                                HStack {
                                    ZStack {
                                        Circle()
                                            .frame(width: 40, height: 40)
                                            .foregroundStyle(.ultraThinMaterial)
                                            .foregroundColor(Color.navy)
                                        Image(systemName: item.icon)
                                            .foregroundColor(Color.white)
                                            .padding(.horizontal, 20)
                                    }
                                    
                                    Text(item.title).bold()
                                        .font(.system(size: 25))
                                        .foregroundStyle(Color.white)
                                        .padding(.leading, 10)
                                    Spacer()
                                }
                                .frame(height: rowHeight)
                            }
                        }
                    }
                    .frame(width: 265)
                    Spacer()
                }
                .ignoresSafeArea(.all)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .animation(.easeOut(duration: 0.25), value: showMenu)
    }
}

struct Icon: View {
    var icon = ""
    var body: some View {
        ZStack {
            Circle()
                .frame(width: 50, height: 50)
                .foregroundStyle(.ultraThinMaterial)
            Image(systemName: icon)
                .foregroundColor(Color.white)
        }
    }
}
