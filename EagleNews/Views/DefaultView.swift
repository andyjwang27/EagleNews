import SwiftUI

public struct DefaultView<Trailing: View>: View {
    // Base Model of all Pages
    var title: String
    @Binding var showMenu: Bool
    let showMenuButton: Bool
    @ViewBuilder let trailing: () -> Trailing
    @State private var reveal = false
    
    init(
        title: String,
        showMenu: Binding<Bool>,
        showMenuButton: Bool,
        @ViewBuilder trailing: @escaping () -> Trailing = { EmptyView() }
    ) {
        self.title = title
        self._showMenu = showMenu
        self.showMenuButton = showMenuButton
        self.trailing = trailing
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            ZStack {
                Color.blue2
                    .frame(maxWidth: .infinity, maxHeight: 150)
                    .edgesIgnoringSafeArea(.top)
                    .overlay(
                        VStack {
                            Rectangle()
                                .fill(Color.navy)
                                .frame(height: 20, alignment: .bottom)
                            Spacer().frame(height: 110)
                            Rectangle()
                                .fill(Color.navy)
                                .frame(height: 20, alignment: .top)
                        }
                    )
                HStack {
                    HStack {
                        if showMenuButton {
                            RoundButton(imageName: "list.bullet", background: .navy) {
                                showMenu.toggle()
                            }
                            .padding(.leading, 40)
                        }
                    }
                    .frame(width: 44)
                    
                    Spacer()
                    
                    Text(title)
                        .foregroundStyle(Color.white)
                        .font(.custom("ARIAL", size: 40))
                        .bold()
                        .frame(maxWidth: .infinity, alignment: .center)
                        .multilineTextAlignment(.center)
                        .mask(
                            Rectangle()
                                .frame(width: reveal ? 370 : 0, height: 150)
                                .animation(.easeOut(duration: 1.5), value: reveal)
                        )
                        .onAppear {
                            reveal = true
                        }
                    Spacer()
                    
                    HStack {
                        trailing()
                            .padding(.trailing, 40)
                    }
                    .frame(width: 44)
                }
            }
            
            Color.white
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            Color.blue2
                .frame(height: 150)
                .overlay(
                    VStack {
                        Rectangle()
                            .fill(Color.navy)
                            .frame(height: 20, alignment: .bottom)
                        Spacer().frame(height: 110)
                        Rectangle()
                            .fill(Color.navy)
                            .frame(height: 20, alignment: .top)
                    }
                )
        }
        .ignoresSafeArea(.container, edges: [.top, .bottom])
        .background(Color.white)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        
    }
}
