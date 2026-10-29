import SwiftUI
import FirebaseMessaging
import FirebaseAuth
import FirebaseFirestore

struct ProfileSettingsView: View {
    @FocusState var isTyping: Bool
    @StateObject var viewModel = ProfileSettingsViewViewModel()
    @State private var notificationsEnabled = false
    @EnvironmentObject private var authViewModel: ContentViewViewModel
    @Environment(\.dismiss) private var dismiss
    private let characterLimit = 50
    
    @State private var liveToken = "Loading..."
    @State private var apnsStatus = "Unknown"
    @State private var notificationStatus = "Unknown"
    @State private var uid = "Unknown"
    @State private var storedToken = "Loading..."
    
    var body: some View {
        ZStack {
            DefaultView(title: "Settings", showMenu: .constant(false), showMenuButton: false) {
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
                Section("Notifications") {
                    HStack(spacing: 0) {
                        Text("Request Notifications")
                        Spacer()
                        TLButton(title: "Request", background: .navy) {
                            viewModel.requestNotificationPermission()
                        }
                        .frame(width: 115)
                        .padding(.leading, 50)
                        .padding(.trailing, 20)
                    }
                }
                Section("User Settings") {
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
                            .listRowInsets(EdgeInsets(top: 0, leading: 16, bottom: 0, trailing: 16))
                        TLButton(title: "Save", background: .navy) {
                            viewModel.save { success in
                                if success {
                                    viewModel.activeAlert = .success
                                    viewModel.name = ""
                                } else {
                                    viewModel.activeAlert = .error
                                }
                            }
                        }
                        .frame(width: 100)
                        .padding(.leading, 10)
                    }
                    Text("\(viewModel.name.count) / \(characterLimit)")
                    HStack(spacing: 0) {
                        Button {
                            authViewModel.logout()
                        } label: {
                            buttonLabel(title: "Logout", imageName: "person.slash", background: .navy)
                        }
                        .frame(width: 120)
                        .buttonStyle(.borderless)
                        .padding(.trailing, 10)
                        Spacer()
                        Button {
                            viewModel.startDeleteFlow()
                        } label: {
                            buttonLabel(title: "Delete Account", imageName: "trash", background: .navy)
                        }
                        .frame(width: 150)
                        .buttonStyle(.borderless)
                        .padding(.leading, 10)
                    }
                }
                .listRowSeparator(.hidden)
                /*Section("Debug") {
                    Button("Refresh Debug Info") {
                        uid = Auth.auth().currentUser?.uid ?? "nil"

                        liveToken = Messaging.messaging().fcmToken ?? "nil"

                        apnsStatus =
                            UserDefaults.standard.bool(forKey: "HasAPNSToken")
                            ? "YES"
                            : "NO"

                        UNUserNotificationCenter.current()
                            .getNotificationSettings { settings in
                                DispatchQueue.main.async {
                                    notificationStatus =
                                        "\(settings.authorizationStatus)"
                                }
                            }

                        if let uid = Auth.auth().currentUser?.uid {
                            Firestore.firestore()
                                .collection("users")
                                .document(uid)
                                .getDocument { snapshot, error in
                                    DispatchQueue.main.async {
                                        storedToken =
                                            snapshot?
                                                .data()?["fcmToken"]
                                            as? String ?? "nil"
                                    }
                                }
                        }
                    }

                    Text("UID:")
                    Text(uid)
                        .font(.caption2)
                        .textSelection(.enabled)

                    Text("APNs: \(apnsStatus)")
                        .font(.caption2)

                    Text("Notifications: \(notificationStatus)")
                        .font(.caption2)

                    Text("Topics:")
                    if let user = viewModel.user {
                        Text(
                            user.activeRoles
                                .map { viewModel.topicName($0) }
                                .joined(separator: ", ")
                        )
                        .font(.caption2)
                    }

                    Text("Live FCM:")
                    Text(liveToken)
                        .font(.caption2)
                        .textSelection(.enabled)

                    Text("Stored FCM:")
                    Text(storedToken)
                        .font(.caption2)
                        .textSelection(.enabled)
                }
                .listRowSeparator(.hidden)*/
            }
            .padding(.top, 150)
            .padding(.bottom, 150)
            .ignoresSafeArea(.container, edges: [.top, .bottom])
            .ignoresSafeArea(.keyboard)
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
                        message: Text("Name saved successfully."),
                        dismissButton: .default(Text("OK")) {
                            isTyping = false
                        }
                    )
                }
            }
            .onAppear {
                viewModel.fetchUser()
            }
            if viewModel.deleteState != .idle {
                Color.black.opacity(0.6)
                    .ignoresSafeArea()

                VStack(spacing: 20) {

                    switch viewModel.deleteState {

                    case .confirmStart:
                        Text("Delete Account?")
                            .font(.custom("ARIAL", size: 15))
                            .foregroundColor(Color.white)

                        Text("This action cannot be undone.")
                            .font(.custom("ARIAL", size: 15))
                            .foregroundColor(Color.white)
                        
                        Button {
                            viewModel.beginCountdown()
                        } label: {
                            buttonLabel(title: "Start 5 second countdown", imageName: "checkmark", background: .navy)
                        }
                        .frame(width: 200)

                        Button {
                            viewModel.cancelDelete()
                        } label: {
                            buttonLabel(title: "Cancel", imageName: "xmark", background: .darkRed)
                        }
                        .frame(width: 200)

                    case .countingDown:
                        Text("Deleting in \(viewModel.countdown)")
                            .font(.custom("ARIAL", size: 15))
                            .foregroundColor(Color.white)

                        Text("You can cancel anytime.")
                            .font(.custom("ARIAL", size: 15))
                            .foregroundColor(Color.white)

                        Button {
                            viewModel.cancelDelete()
                        } label: {
                            buttonLabel(title: "Cancel", imageName: "xmark", background: .darkRed)
                        }
                        .frame(width: 200)

                    case .readyToDelete:
                        Text("Ready to delete account")
                            .font(.custom("ARIAL", size: 15))
                            .foregroundColor(Color.white)

                        Button(role: .destructive) {
                            Task {
                                await viewModel.deleteAccount()
                            }
                        } label: {
                            buttonLabel(title: "Delete Now", imageName: "checkmark", background: .navy)
                        }
                        .frame(width: 200)

                        Button {
                            viewModel.cancelDelete()
                        } label: {
                            buttonLabel(title: "Cancel", imageName: "xmark", background: .darkRed)
                        }
                        .frame(width: 200)

                    case .idle:
                        EmptyView()
                    }
                }
                .padding()
                .frame(maxWidth: 300)
                .background(.ultraThinMaterial)
                .cornerRadius(20)
                .shadow(radius: 20)
                .transition(.scale)
            }
        }
        .animation(.easeInOut, value: viewModel.deleteState)
    }
}

private func buttonLabel(title: String, imageName: String, background: Color) -> some View {
    ZStack {
        RoundedRectangle(cornerRadius: 10)
            .foregroundColor(background)

        HStack(spacing: 10) {
            Text(title)
                .foregroundColor(.white)
                .font(.system(size: 15))
                .bold()

            Image(systemName: imageName)
                .foregroundColor(.white)
        }
    }
    .frame(height: 45)
}
