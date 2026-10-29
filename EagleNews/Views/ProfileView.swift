import SwiftUI
import FirebaseAuth
import FirebaseMessaging

public struct ProfileView: View {
    @Binding var showMenu: Bool
    @StateObject private var viewModel = ProfileViewViewModel()
    @EnvironmentObject var authViewModel: ContentViewViewModel
    @Environment(\.openURL) private var openURL
    @State private var subscribedTopics: [String] = []
    
    public var body: some View {
        ZStack {
            DefaultView(title: "Profile", showMenu: $showMenu, showMenuButton: true) {
                RoundButton(imageName: "gearshape", background: .navy) {
                    viewModel.showingSettingsView = true
                }
            }
            
            if let user = viewModel.user {
                VStack(alignment: .center){
                    Image(systemName: "person.circle")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .foregroundColor(Color.navy)
                        .frame(width: 125, height: 125)
                        .padding(.bottom, 20)
                    VStack(alignment: .leading) {
                        HStack {
                            Text("Name: ")
                                .bold()
                            Text(user.name)
                        }
                        HStack {
                            Text("Email: ")
                                .bold()
                            Text(user.email)
                        }
                        HStack {
                            Text("Member Since: ")
                                .bold()
                            Text("\(Date(timeIntervalSince1970: user.joined).formatted(date: .abbreviated, time: .shortened))")
                        }
                        Text("")
                        HStack {
                            Text("Roles: ")
                                .bold()
                            Text(user.activeRoles
                                .map { $0.formattedString() }
                                .joined(separator: ", ").formattedString()
                            )
                                .frame(maxWidth: 275, alignment: .leading)
                                .lineLimit(nil)
                        }
                    }
                    .padding(.vertical)
                    .padding(.bottom, 30)
                }
                .padding(.top, 150)
                .padding(.bottom, 150)
                .ignoresSafeArea(.container, edges: [.top, .bottom])
            } else {
                Text("Loading Profile...")
            }
                
        }
        .sheet(isPresented: $viewModel.showingSettingsView, onDismiss: {
            viewModel.fetchUser()
        }) {
            ProfileSettingsView()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(.white)
        .onAppear {
            viewModel.fetchUser()
        }
    }
}
