import SwiftUI
import FirebaseAuth
import FirebaseFirestore

struct EditEventView: View {
    @FocusState var isTyping: Bool
    @StateObject private var viewModel: EditEventViewViewModel
    @EnvironmentObject private var idModel: ContentViewViewModel
    @Environment(\.dismiss) private var dismiss
    
    @FirestoreQuery(collectionPath: "roles") var roles: [RoleItem]
    @FirestoreQuery(collectionPath: "users") var users: [User]
    @State private var selectedRoles: Set<RoleItem> = []
    @State private var activeSheet: AssignSheet?
    
    @State private var events: [EventItem] = []
    @State private var bottomOffset: CGFloat = 145
    @State private var contentOpacity: Double = 0
    
    let currentUserId = Auth.auth().currentUser?.uid
    
    init(event: EventItem) {
        _viewModel = StateObject(wrappedValue: EditEventViewViewModel(event: event))
    }
    
    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                Color.blue2
                    .frame(maxWidth: .infinity, maxHeight: 20)
                CustomCalendarView(selectedDate: $viewModel.selectedDate, events: events)
                    .padding(.bottom, 20)
                ScrollView(.vertical, showsIndicators: true) {
                    ZStack {
                        Color.clear
                            .contentShape(Rectangle())
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                            .onTapGesture {
                                isTyping = false
                            }
                        VStack(spacing: 0) {
                            TextField("Event Title", text: $viewModel.title, axis: .vertical)
                                .textFieldStyle(OutlinedTextFieldStyle())
                                .background(Color.white)
                                .autocorrectionDisabled()
                                .textInputAutocapitalization(.never)
                                .focused($isTyping)
                                .padding(.vertical, 20)
                                .padding(.horizontal, 30)
                                .opacity(contentOpacity)
                            HStack(spacing: 0) {
                                Spacer().frame(width: 30)
                                ZStack {
                                    Text("Start Time:")
                                        .font(.custom("ARIAL", size: 20))
                                        .padding(.all, 15)
                                        .background(Color.navy)
                                        .clipShape(RoundedRectangle(cornerRadius: 12))
                                    Text("Start Time:")
                                        .font(.custom("ARIAL", size: 20))
                                        .padding(.all, 11)
                                        .foregroundColor(.white)
                                        .background(Color.blue2)
                                        .clipShape(RoundedRectangle(cornerRadius: 8))
                                }
                                Spacer().frame(width: 20)
                                DatePicker("", selection: $viewModel.startTime, displayedComponents: [.hourAndMinute])
                                    .fixedSize()
                                Spacer()
                            }
                            .padding(.bottom, 10)
                            HStack(spacing: 0) {
                                Spacer().frame(width: 30)
                                ZStack {
                                    Text("End Time: ")
                                        .font(.custom("ARIAL", size: 20))
                                        .padding(.all, 15)
                                        .background(Color.navy)
                                        .clipShape(RoundedRectangle(cornerRadius: 12))
                                    Text("End Time: ")
                                        .font(.custom("ARIAL", size: 20))
                                        .padding(.all, 11)
                                        .foregroundColor(.white)
                                        .background(Color.blue2)
                                        .clipShape(RoundedRectangle(cornerRadius: 8))
                                }
                                Spacer().frame(width: 20)
                                DatePicker("", selection: $viewModel.endTime, displayedComponents: [.hourAndMinute])
                                    .fixedSize()
                                Spacer()
                            }
                            .padding(.bottom, 10)
                            VStack(alignment: .leading, spacing: 0) {
                                HStack(spacing: 0) {
                                    timeButton(text: "+5 Min") {
                                        viewModel.endTime = Calendar.current.date(
                                            byAdding: .minute,
                                            value: 5,
                                            to: viewModel.startTime) ?? viewModel.startTime
                                    }
                                    .padding(.leading, 30)
                                    Spacer()
                                    timeButton(text: "+15 Min") {
                                        viewModel.endTime = Calendar.current.date(
                                            byAdding: .minute,
                                            value: 15,
                                            to: viewModel.startTime) ?? viewModel.startTime
                                    }
                                    Spacer()
                                    timeButton(text: "+30 Min") {
                                        viewModel.endTime = Calendar.current.date(
                                            byAdding: .minute,
                                            value: 30,
                                            to: viewModel.startTime) ?? viewModel.startTime
                                    }
                                    Spacer()
                                    timeButton(text: "+1 Hr") {
                                        viewModel.endTime = Calendar.current.date(
                                            byAdding: .minute,
                                            value: 60,
                                            to: viewModel.startTime) ?? viewModel.startTime
                                    }
                                    .padding(.trailing, 30)
                                }
                            }
                            .padding(.bottom, 20)
                            ZStack {
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color.navy, lineWidth: 8)
                                    .fill(Color.blue2)
                                    .frame(maxWidth: .infinity)
                                    .frame(height: 130)
                                    .padding(.horizontal, 30)
                                VStack(alignment: .center, spacing: 0) {
                                    HStack(spacing: 0) {
                                        colorButton(buttonColor: .calendarOrange, isSelected: viewModel.selectedColor == "calendarOrange") {
                                            viewModel.selectedColor = "calendarOrange"
                                        }
                                        .padding(.leading, 50)
                                        Spacer()
                                        colorButton(buttonColor: .calendarYellow, isSelected: viewModel.selectedColor == "calendarYellow") {
                                            viewModel.selectedColor = "calendarYellow"
                                        }
                                        Spacer()
                                        colorButton(buttonColor: .calendarGreen, isSelected: viewModel.selectedColor == "calendarGreen") {
                                            viewModel.selectedColor = "calendarGreen"
                                        }
                                        Spacer()
                                        colorButton(buttonColor: .calendarGray, isSelected: viewModel.selectedColor == "calendarGray") {
                                            viewModel.selectedColor = "calendarGray"
                                        }
                                        .padding(.trailing, 50)
                                    }
                                    Spacer().frame(height: 20)
                                    HStack(spacing: 0) {
                                        colorButton(buttonColor: .calendarLightBlue, isSelected: viewModel.selectedColor == "calendarLightBlue") {
                                            viewModel.selectedColor = "calendarLightBlue"
                                        }
                                        .padding(.leading, 50)
                                        Spacer()
                                        colorButton(buttonColor: .calendarBlue, isSelected: viewModel.selectedColor == "calendarBlue") {
                                            viewModel.selectedColor = "calendarBlue"
                                        }
                                        Spacer()
                                        colorButton(buttonColor: .calendarDarkBlue, isSelected: viewModel.selectedColor == "calendarDarkBlue") {
                                            viewModel.selectedColor = "calendarDarkBlue"
                                        }
                                        Spacer()
                                        colorButton(buttonColor: .calendarPurple, isSelected: viewModel.selectedColor == "calendarPurple") {
                                            viewModel.selectedColor = "calendarPurple"
                                        }
                                        .padding(.trailing, 50)
                                    }
                                }
                            }
                            .padding(.bottom, 20)
                            
                            DatePicker("Select Time", selection: $viewModel.chosenTime, displayedComponents: .hourAndMinute)
                                .datePickerStyle(.wheel)
                                .labelsHidden()
                                .environment(\.locale, Locale(identifier:"en_GB"))
                                .padding(.horizontal, 30)
                                .frame(height: 150)
                                .clipped()
                                .padding(.bottom, 20)
                            HStack(spacing: 0) {
                                Text("Pre-Notification Time: \(viewModel.displayTime)")
                                Spacer()
                            }
                            .padding(.horizontal, 30)
                            .padding(.bottom, 20)
                            
                            /*if idModel.isAdmin || idModel.isClubLeader {
                                Toggle("Group Event?", isOn: $isGroupEvent)
                                    .padding(.horizontal, 30)
                                    .padding(.bottom, 20)
                            }*/
                            
                            if viewModel.isGroup {
                                HStack(spacing: 12) {
                                    Button {
                                        activeSheet = .selectUsers
                                    } label: {
                                        buttonLabel(title: "Select Users")
                                            .contentShape(Rectangle())
                                    }
                                    .buttonStyle(.borderless)
                                    Spacer()
                                    Button {
                                        activeSheet = .selectRoles
                                    } label: {
                                        buttonLabel(title: "Select Roles")
                                            .contentShape(Rectangle())
                                    }
                                    .buttonStyle(.borderless)
                                }
                                .padding(.horizontal, 30)
                                if !viewModel.selectedUsers.isEmpty {
                                    HStack(spacing: 0) {
                                        Text("Selected Users:")
                                            .font(.custom("ARIAL.ttf", size: 18))
                                            .padding(.vertical, 20)
                                            .padding(.leading, 30)
                                        Spacer()
                                    }
                                    ZStack {
                                        RoundedRectangle(cornerRadius: 15)
                                            .stroke(Color.navy, lineWidth: 10)
                                            .fill(Color.blue2)
                                            .frame(maxWidth: .infinity)
                                            .padding(.horizontal, 30)
                                        WrappingHStack(horizontalSpacing: 8, verticalSpacing: 10) {
                                            ForEach(Array(viewModel.selectedUsers)) { user in
                                                HStack(spacing: 6) {
                                                    if user.id == currentUserId{
                                                        HStack(spacing: 10) {
                                                            Text("\(user.name.formattedString()) (You)")
                                                                .lineLimit(2)
                                                                .minimumScaleFactor(0.8)
                                                                .foregroundColor(.navy)
                                                                .font(.system(size: 15))
                                                        }
                                                    } else {
                                                        Text(user.name.formattedString())
                                                            .lineLimit(2)
                                                            .minimumScaleFactor(0.8)
                                                            .foregroundColor(.navy)
                                                            .font(.system(size: 15))
                                                    }
                                                    Button {
                                                        viewModel.selectedUsers.remove(user)
                                                    } label: {
                                                        Image(systemName: "xmark")
                                                            .tint(.navy)
                                                    }
                                                    .buttonStyle(.borderless)
                                                }
                                                .padding(.horizontal, 12)
                                                .padding(.vertical, 8)
                                                .background(Color.white)
                                                .clipShape(RoundedRectangle(cornerRadius: 20))
                                            }
                                        }
                                        .fixedSize(horizontal: false, vertical: true)
                                        .padding(.horizontal, 40)
                                        .padding(.vertical, 10)
                                    }
                                    .padding(.bottom, 10)
                                }
                                if !viewModel.roleNames.isEmpty {
                                    HStack(spacing: 0) {
                                        Text("Selected Roles:")
                                            .font(.custom("ARIAL.ttf", size: 18))
                                            .padding(.vertical, 20)
                                            .padding(.leading, 30)
                                        Spacer()
                                    }
                                    ZStack {
                                        RoundedRectangle(cornerRadius: 15)
                                            .stroke(Color.navy, lineWidth: 10)
                                            .fill(Color.blue2)
                                            .frame(maxWidth: .infinity)
                                            .padding(.horizontal, 30)
                                        WrappingHStack(horizontalSpacing: 8, verticalSpacing: 10) {
                                            ForEach(viewModel.roleNames, id: \.self) { roleName in
                                                HStack(spacing: 6) {
                                                    Text(roleName.formattedString())
                                                    Button {
                                                        viewModel.roleNames.removeAll { $0 == roleName }
                                                    } label: {
                                                        Image(systemName: "xmark")
                                                            .tint(.navy)
                                                    }
                                                    .buttonStyle(.borderless)
                                                }
                                                .padding(.horizontal, 12)
                                                .padding(.vertical, 8)
                                                .background(Color.white)
                                                .clipShape(RoundedRectangle(cornerRadius: 20))
                                            }
                                        }
                                        .fixedSize(horizontal: false, vertical: true)
                                        .padding(.horizontal, 40)
                                        .padding(.vertical, 10)
                                    }
                                }
                            }
                        }
                        .padding(.bottom, 20)
                    }
                    .opacity(contentOpacity)
                }
                .frame(maxHeight: .infinity)
                ZStack {
                    VStack(spacing: 0) {
                        Color.navy
                            .frame(maxWidth: .infinity, maxHeight: 20)
                        Color.blue2
                            .frame(maxWidth: .infinity, maxHeight: 125)
                    }
                    .offset(y: bottomOffset)
                    HStack(spacing: 0) {
                        if viewModel.isGroup {
                            RoundButton(imageName: "checkmark", background: .navy) {
                                viewModel.updateEvent()
                            }
                            .padding(.leading, 40)
                        } else {
                            RoundButton(imageName: "checkmark", background: .navy) {
                                viewModel.roleNames = []
                                viewModel.selectedUsers = []
                                viewModel.updateEvent()
                            }
                            .padding(.leading, 40)
                        }
                        Spacer()
                        RoundButton(imageName: "xmark", background: .navy) {
                            dismiss()
                        }
                        .padding(.trailing, 40)
                    }
                    .opacity(contentOpacity)
                }
            }
            .ignoresSafeArea(.container, edges: [.top, .bottom])
            .onAppear {
                //viewModel.fetchUser()
                withAnimation(.easeOut(duration: 0.5)) {
                    bottomOffset = 0
                }
                withAnimation(.easeOut(duration: 0.5).delay(0.6)) {
                    contentOpacity = 1.0
                }
                if viewModel.isGroup {
                    viewModel.selectedUsers = Set(
                        users.filter { viewModel.recipientIds.contains($0.id) }
                    )
                }
                if viewModel.isGroup {
                       selectedRoles = Set(
                           roles.filter { role in
                               viewModel.roleNames.contains(role.name)
                           }
                       )
                   }
            }
            .onChange(of: selectedRoles) {
                viewModel.roleNames = selectedRoles.map(\.name)
            }
            .sheet(item: $activeSheet) { sheet in
                switch sheet {
                case .selectUsers:
                    SelectUsersView(selected: $viewModel.selectedUsers)
                case .selectRoles:
                    SelectRolesView(selected: $selectedRoles)
                }
            }
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
                        message: Text("Event saved successfully."),
                        dismissButton: .default(Text("OK")) {
                            isTyping = false
                            dismiss()
                        }
                    )
                }
            }
        }
        .ignoresSafeArea(.container, edges: [.top, .bottom])
        .scrollContentBackground(.hidden)
    }
}

private func buttonLabel(title: String) -> some View {
    ZStack {
        RoundedRectangle(cornerRadius: 10)
            .foregroundColor(.navy)

        HStack(spacing: 10) {
            Text(title)
                .foregroundColor(.white)
                .font(.system(size: 15))
                .bold()

            Image(systemName: "plus")
                .foregroundColor(.white)
        }
    }
    .frame(height: 45)
}
