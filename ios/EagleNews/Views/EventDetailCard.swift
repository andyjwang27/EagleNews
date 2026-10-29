import SwiftUI
import FirebaseCore

struct EventDetailCard: View {
    @StateObject var viewModel: EventDetailCardViewModel
    @State private var showEditView: Bool = false
    @State private var eventToDelete: EventItem?
    @State private var isPinned: Bool
    
    private let userId: String
    let event: EventItem
    let isGroupEvent: Bool
    let onDelete: () -> Void
    
    init (userId: String, event: EventItem, isGroupEvent: Bool, onDelete: @escaping () -> Void = {}) {
        self.userId = userId
        self.event = event
        self.isGroupEvent = isGroupEvent
        self.onDelete = onDelete
        self._isPinned = State(initialValue: event.pinned)
        self._viewModel = StateObject(wrappedValue: EventDetailCardViewModel(userId: userId))
        
    }
    

    private var start: Date {
        event.startDate.dateValue()
    }

    private var end: Date {
        event.endDate.dateValue()
    }
    
    private var eventColor: Color {
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

    var body: some View {
        if isGroupEvent {
            VStack(alignment: .leading, spacing: 0) {
                HStack(spacing: 0) {
                    Text(event.title)
                        .font(.custom("ARIAL", size: 24))
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                    Spacer()
                    if isPinned {
                        Image(systemName: "pin.fill")
                            .frame(width: 35, height: 35)
                            .foregroundColor(Color.white)
                    }
                }
                
                HStack(spacing: 5) {
                    Text("From:")
                        .fontWeight(.bold)
                    Text(start, style: .time)
                }
                .font(.custom("ARIAL", size: 14))
                .foregroundColor(.white)
                .padding(.top, 15)
                
                HStack(spacing: 5) {
                    Text("To:")
                        .fontWeight(.bold)
                    
                    Text(end, style: .time)
                }
                .font(.custom("ARIAL", size: 14))
                .foregroundColor(.white)
                .padding(.top, 5)
                
                Divider()
                    .padding(.vertical, 15)
                
                HStack(spacing: 0) {
                    VStack(alignment: .leading, spacing: 0) {
                        Text("Published by")
                            .font(.custom("ARIAL", size: 12))
                            .foregroundColor(.white)
                            .padding(.bottom, 5)
                        Text(event.publisher)
                            .font(.custom("ARIAL", size: 15))
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                    }
                    Spacer()
                    DetailButton(imageName: "pencil") {
                        showEditView = true
                    }
                    .padding(.trailing, 10)
                    DetailButton(imageName: isPinned ? "pin.slash.fill":"pin.fill") {
                        isPinned.toggle()
                        viewModel.togglePin(event: event)
                    }
                    .padding(.trailing, 10)
                    DetailButton(imageName: "trash") {
                        guard event.id != nil else {
                            return
                        }
                        eventToDelete = event
                    }
                }
            }
            .padding(25)
            .frame(maxWidth: 350)
            .background(eventColor)
            .clipShape(
                RoundedRectangle(cornerRadius: 18)
            )
            .sheet(isPresented: $showEditView) {
                EditEventView(event: event)
            }
            .alert("Delete Event?", isPresented: Binding(
                get: { eventToDelete != nil },
                set: { if !$0 { eventToDelete = nil } }
            )) {
                Button("Delete", role: .destructive) {
                    if let event = eventToDelete {
                        viewModel.delete(id: event.id ?? "")
                        onDelete()
                    }
                    eventToDelete = nil
                }

                Button("Cancel", role: .cancel) {
                    eventToDelete = nil
                }
            } message: {
                Text("This action cannot be undone.")
            }
        } else {
            VStack(alignment: .leading, spacing: 0) {
                HStack(spacing: 0) {
                    Text(event.title)
                        .font(.custom("ARIAL", size: 24))
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                    Spacer()
                    if isPinned {
                        Image(systemName: "pin.fill")
                            .frame(width: 35, height: 35)
                            .foregroundColor(Color.white)
                    }
                }
                
                HStack(spacing: 5) {
                    Text("From:")
                        .fontWeight(.bold)
                    
                    Text(start, style: .time)
                }
                .font(.custom("ARIAL", size: 14))
                .foregroundColor(.white)
                .padding(.top, 5)
                
                HStack(spacing: 5) {
                    Text("To:")
                        .fontWeight(.bold)
                    
                    Text(end, style: .time)
                }
                .font(.custom("ARIAL", size: 14))
                .foregroundColor(.white)
                .padding(.top, 5)
                
                Divider()
                    .padding(.vertical, 15)
                
                HStack(spacing: 0) {
                    VStack(alignment: .leading, spacing: 0) {
                        Text("Published by")
                            .font(.custom("ARIAL", size: 12))
                            .foregroundColor(.white)
                            .padding(.bottom, 5)
                        Text(event.publisher)
                            .font(.custom("ARIAL", size: 15))
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                    }
                    Spacer()
                    DetailButton(imageName: "pencil") {
                        showEditView = true
                    }
                    .padding(.trailing, 10)
                    DetailButton(imageName: isPinned ? "pin.slash.fill":"pin.fill") {
                        isPinned.toggle()
                        viewModel.togglePin(event: event)
                    }
                    .padding(.trailing, 10)
                    DetailButton(imageName: "trash") {
                        guard event.id != nil else {
                            return
                        }
                        eventToDelete = event
                    }
                }
            }
            .padding(25)
            .frame(maxWidth: 350)
            .background(eventColor)
            .clipShape(
                RoundedRectangle(cornerRadius: 18)
            )
            .shadow(radius: 25)
            .sheet(isPresented: $showEditView) {
                EditEventView(event: event)
            }
            .alert("Delete Event?", isPresented: Binding(
                get: { eventToDelete != nil },
                set: { if !$0 { eventToDelete = nil } }
            )) {
                Button("Delete", role: .destructive) {
                    if let event = eventToDelete {
                        viewModel.delete(id: event.id ?? "")
                        onDelete()
                    }

                    eventToDelete = nil
                }

                Button("Cancel", role: .cancel) {
                    eventToDelete = nil
                }
            } message: {
                Text("This action cannot be undone.")
            }
        }
    }
}

struct ListEventDetailCard: View {
    @StateObject var viewModel: EventDetailCardViewModel
    @State private var showEditView: Bool = false
    @State private var eventToDelete: EventItem?
    @State private var isPinned: Bool
    
    private let userId: String
    let event: EventItem
    let onDelete: () -> Void
    
    init (userId: String, event: EventItem, onDelete: @escaping () -> Void = {}) {
        self.userId = userId
        self.event = event
        self.onDelete = onDelete
        self._isPinned = State(initialValue: event.pinned)
        self._viewModel = StateObject(wrappedValue: EventDetailCardViewModel(userId: userId))
    }

    private var start: Date {
        event.startDate.dateValue()
    }

    private var end: Date {
        event.endDate.dateValue()
    }
    
    private var eventColor: Color {
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

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 0) {
                Text(event.title)
                    .font(.custom("ARIAL", size: 24))
                    .fontWeight(.bold)
                    .foregroundColor(.white)
                Spacer()
                if isPinned {
                    Image(systemName: "pin.fill")
                        .frame(width: 35, height: 35)
                        .foregroundColor(Color.white)
                }
            }
            
            Text(start, style: .date)
                .fontWeight(.bold)
                .font(.custom("ARIAL", size: 14))
                .foregroundColor(.white)
                .padding(.top, 5)
            
            HStack(spacing: 5) {
                Text("From:")
                    .fontWeight(.bold)
                
                Text(start, style: .time)
            }
            .font(.custom("ARIAL", size: 14))
            .foregroundColor(.white)
            .padding(.top, 5)
                
            HStack(spacing: 5) {
                Text("To:")
                    .fontWeight(.bold)
                
                Text(end, style: .time)
            }
            .font(.custom("ARIAL", size: 14))
            .foregroundColor(.white)
            .padding(.top, 5)
                
            Divider()
                .padding(.vertical, 15)
            
            HStack(spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    Text("Published by")
                        .font(.custom("ARIAL", size: 12))
                        .foregroundColor(.white)
                        .padding(.bottom, 5)
                    Text(event.publisher)
                        .font(.custom("ARIAL", size: 15))
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                }
                Spacer()
                DetailButton(imageName: "pencil") {
                    showEditView = true
                }
                .padding(.trailing, 10)
                DetailButton(imageName: isPinned ? "pin.slash.fill":"pin.fill") {
                    isPinned.toggle()
                    viewModel.togglePin(event: event)
                }
                .padding(.trailing, 10)
                DetailButton(imageName: "trash") {
                    guard event.id != nil else {
                        return
                    }
                    eventToDelete = event
                }
            }
        }
        .padding(25)
        .frame(maxWidth: 350)
        .background(eventColor)
        .clipShape(
            RoundedRectangle(cornerRadius: 18)
        )
        .shadow(radius: 25)
        .sheet(isPresented: $showEditView) {
            EditEventView(event: event)
        }
        .alert("Delete Event?", isPresented: Binding(
            get: { eventToDelete != nil },
            set: { if !$0 { eventToDelete = nil } }
        )) {
            Button("Delete", role: .destructive) {
                if let event = eventToDelete {
                    viewModel.delete(id: event.id ?? "")
                    onDelete()
                }
                eventToDelete = nil
            }

            Button("Cancel", role: .cancel) {
                eventToDelete = nil
            }
        } message: {
            Text("This action cannot be undone.")
        }
    }
}

struct EventGroupDetailView: View {
    @EnvironmentObject var idModel: ContentViewViewModel
    let events: [EventItem]
    let onDismiss: () -> Void

    var body: some View {
        VStack(spacing: 15) {
            HStack {
                Text("\(events.count) Events")
                    .font(.custom("ARIAL", size: 22))
                    .fontWeight(.bold)
                    .foregroundColor(.white)
                Spacer()

                Button {
                    onDismiss()
                } label: {
                    Image(systemName: "xmark")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.white)
                        .frame(width: 35, height: 35)
                        .background(Color.white.opacity(0.15))
                        .clipShape(Circle())
                }
            }

            ScrollView(.vertical, showsIndicators: true) {
                VStack(spacing: 12) {
                    ForEach(events) { event in
                        EventDetailCard(userId: idModel.currentUserId, event: event, isGroupEvent: true)
                    }
                }
            }
        }
        .padding(20)
        .background(Color.blue2)
        .clipShape(
            RoundedRectangle(cornerRadius: 20)
        )
        .shadow(radius: 30)
    }
}

struct MoreEventsCard: View {
    let count: Int
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            VStack(spacing: 4) {
                Text("+ \(count)")
                    .font(.custom("ARIAL", size: 18))
                    .fontWeight(.bold)

                Text("More Events")
                    .font(.custom("ARIAL", size: 11))
            }
            .foregroundColor(.white)
            .frame(maxWidth: .infinity)
            .frame(maxHeight: .infinity)
            .background(Color.navy)
            .clipShape(
                RoundedRectangle(cornerRadius: 8)
            )
        }
        .buttonStyle(.plain)
    }
}

struct DetailButton: View {
    let imageName: String
    let action: () -> Void
    
    var body: some View {
        Button {
            action()
        } label: {
            Image(systemName: "\(imageName)")
                .font(.system(size: 22))
                .frame(width: 10, height: 10)
                .foregroundColor(Color.white)
                .padding()
                .background(Color.blue2)
                .clipShape(Circle())
                .shadow(radius: 4)
        }
    }
}
