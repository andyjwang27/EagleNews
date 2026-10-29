import SwiftUI
import GoogleSignInSwift
import FirebaseAuth

public struct LoginView: View {
    @StateObject var viewModel = LoginViewViewModel()
    
    @State private var topOffset: CGFloat = -300
    @State private var bottomOffset: CGFloat = 250
    @State private var middleOpacity: Double = 0
    @State private var eagleOpacity: Double = 0
        
    public var body: some View {
        ZStack {
            VStack(spacing: 0) {
                Circle()
                    .stroke(Color.navy, lineWidth: 20)
                    .fill(Color.blue2)
                    .frame(maxWidth: .infinity, maxHeight: 560)
                Spacer()
            }
            .offset(y: topOffset)
            VStack(spacing: 0) {
                Color.blue2
                    .frame(maxWidth: .infinity, maxHeight: 270)
                Spacer()
            }
            .offset(y: topOffset)
            VStack(spacing: 0) {
                Image("eagleLogo").resizable()
                    .frame(width: 450, height: 450)
                    .opacity(eagleOpacity)
                Spacer()
            }
            VStack(alignment: .center, spacing: 0) {
                Spacer().frame(height: 150)
                Image("logoOfficial").resizable()
                    .frame(width: 200, height: 200)
                    .mask(
                        Circle()
                            .frame(width: 200, height: 200)
                    )
                    .padding(.bottom, 30)
                    .offset(y: topOffset)
                Text("Welcome")
                    .font(.custom("ARIAL", size: 45))
                    .foregroundColor(Color.navy)
                    .offset(y: topOffset)
                    .bold()
                Spacer()
                VStack(alignment: .leading, spacing: 0) {
                    Text("Login:")
                        .font(.custom("ARIAL", size: 15))
                        .foregroundColor(Color.navy)
                        .padding(.bottom, 10)
                        .frame(width: 250, alignment: .leading)
                    GoogleSignInButton {
                        if ProcessInfo.processInfo.environment["XCODE_RUNNING_FOR_PREVIEWS"] == "1" {
                            print("Preview mode - skipping Google Sign-In")
                        }
                        viewModel.signInWithGoogle()
                    }
                    .frame(width: 250)
                    Spacer().frame(height: 40)
                    HStack(alignment: .center, spacing: 0) {
                        Text("Don't Have An Account?")
                            .font(.custom("ARIAL", size: 15))
                            .foregroundColor(Color.navy)
                            .frame(alignment: .leading)
                            .padding(.trailing, 20)
                        RoundButton(imageName: "arrow.right", background: Color.navy) {
                            viewModel.showRegister = true
                        }
                    }
                }
                .opacity(middleOpacity)
                .padding(.top, 100)
                .padding(.bottom, 50)
                Spacer()
                Color.blue2
                    .frame(maxWidth: .infinity, maxHeight: 200)
                    .overlay(
                        VStack(spacing: 0) {
                            Color.navy
                                .frame(maxWidth: .infinity, maxHeight: 10, alignment: .top)
                            Spacer()
                        }
                    )
                    .offset(y: bottomOffset)
            }
        }
        .sheet(isPresented: $viewModel.showRegister) {
            RegisterView(showRegister: $viewModel.showRegister)
        }
        .alert(viewModel.errorMessage, isPresented: $viewModel.showAlert) {
            Button("OK", role: .cancel) { }
        }
        .onAppear {
            withAnimation(.easeOut(duration: 1.0)) {
                topOffset = 0
            }
            withAnimation(.easeOut(duration: 1.0)) {
                bottomOffset = 0
            }
            withAnimation(.easeOut(duration: 1.0).delay(1.0)) {
                middleOpacity = 1
            }
            withAnimation(.easeOut(duration: 1.0).delay(1.0)) {
                eagleOpacity = 0.2
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .ignoresSafeArea(.container, edges: [.all])
    }
}
