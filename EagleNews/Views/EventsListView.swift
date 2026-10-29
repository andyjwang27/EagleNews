import SwiftUI
import FirebaseFirestore
import FirebaseAuth

/*enum EventSheet: Identifiable {
    case editEvent
    case eventView

    var id: String {
        switch self {
        case .editEvent: return "editEvent"
        case .eventView: return "eventView"
        }
    }
}*/

public struct EventsListView: View {
    @Binding var showMenu: Bool
    //@FirestoreQuery var items: [EventItem]
    @State var events: [EventItem] = []
    @StateObject private var viewModel: EventsListViewViewModel
    @EnvironmentObject private var adminModel: ContentViewViewModel
    @State var selectedDate = Date()
    @State private var selectedEvent: EventItem?
    //@State private var activeSheet: EventSheet?
    @Environment(\.dismiss) private var dismiss
    
    private let userId: String
    
    init (userId: String, showMenu: Binding<Bool>, selectedDate: Date) {
        self.selectedDate = selectedDate
        self.userId = userId
        self._showMenu = showMenu
        //self._items = FirestoreQuery(collectionPath: "users/\(userId)/events")
        self._viewModel = StateObject(wrappedValue: EventsListViewViewModel(userId: userId))
    }
    
    var pastEvents: [EventItem] {
        let calendar = Calendar.current
        let startOfToday = calendar.startOfDay(for: Date())
        
        return events.filter { item in
            let eventDate = calendar.startOfDay(for: item.startDate.dateValue())
            return eventDate < startOfToday
        }
        .sorted {
            $0.startDate.dateValue() > $1.startDate.dateValue()
        }
    }
    
    var currentEvents: [EventItem] {
        let calendar = Calendar.current
        let startOfToday = calendar.startOfDay(for: Date())
        
        return events.filter { item in
            let eventDate = calendar.startOfDay(for: item.startDate.dateValue())
            return eventDate >= startOfToday
        } .sorted {
            if $0.pinned == $1.pinned {
                return $0.startDate.dateValue() < $1.startDate.dateValue()
            }
            return $0.pinned && !$1.pinned
        }
    }
    
    public var body: some View {
        ZStack {
            DefaultView(title: "Events", showMenu: $showMenu, showMenuButton: false) {
                RoundButton(imageName: "xmark", background: .navy) {
                    dismiss()
                }
            }
            List{
                if !currentEvents.isEmpty {
                    Section("Events") {
                        ForEach(currentEvents) { event in
                            var eventColor: Color {
                                switch event.color {
                                case "calendarLightBlue":
                                    return .calendarLightBlue
                                case "calendarBlue":
                                    return .calendarBlue
                                case "calendarDarkBlue":
                                    return .calendarDarkBlue
                                case "calendarPurple":
                                    return .calendarPurple
                                case "calendarOrange":
                                    return .calendarOrange
                                case "calendarYellow":
                                    return .calendarYellow
                                case "calendarGreen":
                                    return .calendarGreen
                                case "calendarGray":
                                    return .calendarGray
                                default:
                                    return .calendarBlue
                                }
                            }
                            
                            Button {
                                withAnimation(.spring(response: 0.4, dampingFraction: 0.85)) {
                                    selectedEvent = event
                                }
                                //activeSheet = .eventView
                            } label: {
                                HStack(spacing: 0) {
                                    VStack(alignment: .leading, spacing: 5) {
                                        Text(event.title)
                                            .font(.system(size: 15))
                                            .bold()
                                            .padding(.leading, 20)
                                            .foregroundColor(Color.navy)
                                        Text("\(event.startDate.dateValue(), style: .date)")
                                            .font(.system(size: 15))
                                            .padding(.leading, 10)
                                            .foregroundColor(Color.navy)
                                            .padding(.horizontal, 10)
                                        Text("From: \(event.startDate.dateValue(), style: .time)")
                                            .font(.system(size: 15))
                                            .padding(.leading, 10)
                                            .foregroundColor(Color.navy)
                                            .padding(.horizontal, 10)
                                        Text("To: \(event.endDate.dateValue(), style: .time)")
                                            .font(.system(size: 15))
                                            .padding(.leading, 10)
                                            .padding(.bottom, 10)
                                            .padding(.horizontal, 10)
                                            .foregroundColor(Color.navy)
                                    }
                                    Spacer()
                                    if event.pinned {
                                        Image(systemName: "pin.fill")
                                            .frame(width: 35, height: 35)
                                            .foregroundColor(Color.navy)
                                    }
                                    Rectangle()
                                        .fill(eventColor)
                                        .frame(width: 50, height: 50)
                                        .clipShape(RoundedRectangle(cornerRadius: 18))
                                        .padding(.trailing, 10)
                                }
                            }
                            .padding(.vertical, 6)
                        }
                    }
                }
                if !pastEvents.isEmpty {
                    Section("Past") {
                        ForEach(pastEvents) { event in
                            var eventColor: Color {
                                switch event.color {
                                case "calendarLightBlue":
                                    return .calendarLightBlue
                                case "calendarBlue":
                                    return .calendarBlue
                                case "calendarDarkBlue":
                                    return .calendarDarkBlue
                                case "calendarPurple":
                                    return .calendarPurple
                                case "calendarOrange":
                                    return .calendarOrange
                                case "calendarYellow":
                                    return .calendarYellow
                                case "calendarGreen":
                                    return .calendarGreen
                                case "calendarGray":
                                    return .calendarGray
                                default:
                                    return .calendarBlue
                                }
                            }
                            
                            Button {
                                withAnimation(.spring(response: 0.4, dampingFraction: 0.85)) {
                                    selectedEvent = event
                                }
                                //activeSheet = .eventView
                            } label: {
                                HStack(spacing: 0) {
                                    VStack(alignment: .leading, spacing: 5) {
                                        Text(event.title)
                                            .font(.system(size: 15))
                                            .bold()
                                            .padding(.leading, 20)
                                            .foregroundColor(Color.navy)
                                        Text("\(event.startDate.dateValue(), style: .date)")
                                            .font(.system(size: 15))
                                            .padding(.leading, 10)
                                            .foregroundColor(Color.navy)
                                            .padding(.horizontal, 10)
                                        Text("From: \(event.startDate.dateValue(), style: .time)")
                                            .font(.system(size: 15))
                                            .padding(.leading, 10)
                                            .foregroundColor(Color.navy)
                                            .padding(.horizontal, 10)
                                        Text("To: \(event.endDate.dateValue(), style: .time)")
                                            .font(.system(size: 15))
                                            .padding(.leading, 10)
                                            .padding(.bottom, 10)
                                            .padding(.horizontal, 10)
                                            .foregroundColor(Color.navy)
                                    }
                                    Spacer()
                                    if event.pinned {
                                        Image(systemName: "pin.fill")
                                            .frame(width: 35, height: 35)
                                            .foregroundColor(Color.navy)
                                    }
                                    Rectangle()
                                        .fill(eventColor)
                                        .frame(width: 50, height: 50)
                                        .clipShape(RoundedRectangle(cornerRadius: 18))
                                        .padding(.trailing, 10)
                                }
                            }
                            .padding(.vertical, 6)
                            .opacity(0.4)
                        }
                    }
                }
            }
            .padding(.top, 150)
            .padding(.bottom, 150)
            .ignoresSafeArea(.container, edges: [.top, .bottom])
            .onAppear {
                if let userId = Auth.auth().currentUser?.uid {
                    loadEvents(userId: userId)
                }
            }
            
            if let event = selectedEvent {
                Color.black
                    .opacity(0.45)
                    .ignoresSafeArea()
                    .onTapGesture {
                        withAnimation(.spring(response: 0.4, dampingFraction: 0.85)) {
                            selectedEvent = nil
                        }
                    }
                
                ListEventDetailCard(userId: adminModel.currentUserId, event: event, onDelete: {
                    withAnimation(.spring(response: 0.4, dampingFraction: 0.85)) {
                        selectedEvent = nil
                    }
                })
                .transition(
                    .scale(scale: 0.85)
                    .combined(with: .opacity)
                )
                .zIndex(10)
            }
        }
    }
    
    private func loadEvents(userId: String) {
        let db = Firestore.firestore()

        db.collection("users")
            .document(userId)
            .collection("events")
            .addSnapshotListener { snapshot, error in

                if let error = error {
                    print("Error listening for events: \(error)")
                    return
                }

                guard let documents = snapshot?.documents else {
                    return
                }

                DispatchQueue.main.async {
                    events = documents.compactMap { document in
                        try? document.data(as: EventItem.self)
                    }

                    // Keep the selected event in sync
                    if let selectedID = selectedEvent?.id {
                        selectedEvent = events.first { $0.id == selectedID }
                    }
                }
            }
    }
}
