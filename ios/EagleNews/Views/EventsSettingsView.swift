import SwiftUI

struct EventsSettingsView: View {
    @StateObject private var viewModel = EventsSettingsViewViewModel()
    @Environment(\.dismiss) private var dismiss
    
    public var body: some View {
        ZStack {
            DefaultView(title: "Settings", showMenu: .constant(false), showMenuButton: false) {
                RoundButton(imageName: "xmark", background: .navy) {
                    dismiss()
                }
            }
            Form {
                /*Section("Time Before Event (Hours | Minutes)") {
                    DatePicker("Select Time", selection: $viewModel.chosenTime, displayedComponents: .hourAndMinute)
                        .datePickerStyle(.wheel)
                        .labelsHidden()
                        .environment(\.locale, Locale(identifier:"en_GB"))
                    if let user = viewModel.user {
                        Text("Current Time: \(user.eventMinutesBefore) minutes")
                    }
                    TLButton(title: "Save", background: .navy) {
                        viewModel.save { success in
                            if success {
                                viewModel.activeAlert = .success
                            } else {
                                viewModel.activeAlert = .error
                            }
                        }
                    }
                }*/
            }
            .alert(item: $viewModel.activeAlert) { alert in
                switch alert {
                case .error:
                    return Alert(
                        title: Text("Error"),
                        message: Text(viewModel.errorMessage),
                        dismissButton: .default(Text("OK"))
                    )
                case .success:
                    return Alert(
                        title: Text("Success"),
                        message: Text("Time saved successfully."),
                        dismissButton: .default(Text("OK"))
                    )
                }
            }
            .onAppear {
                viewModel.fetchUser()
            }
            .padding(.top, 150)
            .padding(.bottom, 150)
            .ignoresSafeArea(.container, edges: [.top, .bottom])
            .ignoresSafeArea(.keyboard)
        }
    }
}
