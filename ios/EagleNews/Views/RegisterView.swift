import SwiftUI
import GoogleSignIn
import GoogleSignInSwift
import FirebaseCore

public struct RegisterView: View {
    @StateObject var viewModel = RegisterViewViewModel()
    @Binding var showRegister: Bool
    @FocusState var isTyping: Bool
    @Environment(\.dismiss) private var dismiss
    private let characterLimit = 50
        
    public var body: some View {
        ZStack {
            DefaultView(title: "Register", showMenu: .constant(false), showMenuButton: false)  {
                RoundButton(imageName: "xmark", background: .navy) {
                    dismiss()
                }
            }
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
            Form {
                Section("Register Using Your Apps Account") {
                    HStack(spacing: 0) {
                        TextField("Name", text: $viewModel.name, axis: .vertical)
                            .textFieldStyle(OutlinedTextFieldStyle())
                            .background(Color.white)
                            .autocorrectionDisabled()
                            .textInputAutocapitalization(.never)
                            .focused($isTyping)
                            .onChange(of: viewModel.name) { _, newValue in
                                if newValue.count > characterLimit {
                                    viewModel.name = String(newValue.prefix(characterLimit))
                                }
                            }
                        Text("\(viewModel.name.count) / \(characterLimit)")
                            .padding(.leading, 20)
                    }
                    TextField("Apps Address", text: $viewModel.email, axis: .vertical)
                        .textFieldStyle(OutlinedTextFieldStyle())
                        .background(Color.white)
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                        .focused($isTyping)
                    if !viewModel.name.isEmpty && !viewModel.email.isEmpty && (viewModel.email.contains("@apps.stratford.org") || viewModel.email.contains("rowdieeagle@gmail.com")) {
                        Text("Sign In Using Google:")
                        GoogleSignInButton {
                            if ProcessInfo.processInfo.environment["XCODE_RUNNING_FOR_PREVIEWS"] == "1" {
                                print("Preview mode - skipping Google Sign-In")
                            }
                            
                            registerWithGoogle()
                        }
                    }
                }
                .listRowSeparator(.hidden)
            }
            .padding(.top, 150)
            .padding(.bottom, 150)
            .ignoresSafeArea(.container, edges: [.top, .bottom])
            .alert(item: $viewModel.activeAlert) { alert in
                switch alert {
                case .error:
                    return Alert(
                        title: Text("Error"),
                        message: Text(viewModel.errorMessage)
                    )
                case .success:
                    return Alert(
                        title: Text("Success"),
                        message: Text("You're account has been created."),
                        dismissButton: .default(Text("OK")) {
                            showRegister = false
                        }
                    )
                }
            }
        }
        .ignoresSafeArea(.keyboard)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(.white)
    }
    
    func registerWithGoogle() {
        guard let clientID = FirebaseApp.app()?.options.clientID else { return }
        
        let config = GIDConfiguration(clientID: clientID)
        GIDSignIn.sharedInstance.configuration = config
        
        guard let windowScene = UIApplication.shared.connectedScenes
            .first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene,
              let rootViewController = windowScene.windows
            .first(where: { $0.isKeyWindow })?.rootViewController else {
            return
        }
        
        GIDSignIn.sharedInstance.signIn(withPresenting: rootViewController) { result, error in
            if let error = error {
                print(error.localizedDescription)
                return
            }
            
            guard let user = result?.user, let idToken = user.idToken?.tokenString else {
                return
            }
            
            let accessToken = user.accessToken.tokenString
            
            viewModel.registerWithGoogle(idToken: idToken, accessToken: accessToken)
        }
    }
}
