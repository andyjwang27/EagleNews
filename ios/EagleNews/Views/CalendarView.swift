import SwiftUI
import FirebaseCore
import FirebaseFirestore
import FirebaseAuth

public struct CalendarView: View {
    @StateObject var viewModel = CalendarViewViewModel()
    @EnvironmentObject private var idModel: ContentViewViewModel
    @Binding var showMenu: Bool
    @State var selectedDate = Date()
    @State private var events: [EventItem] = []
    @State private var selectedEvent: EventItem?
    @State private var selectedEventGroup: [EventItem]?

    @State private var bottomOffset: CGFloat = 145
    @State private var contentOpacity: Double = 0
    
    var eventDates: Set<Date> {
        let calendar = Calendar.current

        return Set(
            events.map {
                calendar.startOfDay(for: $0.startDate.dateValue())
            }
        )
    }
    
    private var dayEvents: [EventItem] {
        let calendar = Calendar.current
        
        return events.filter {
            calendar.isDate(
                $0.startDate.dateValue(),
                inSameDayAs: selectedDate
            )
        }
    }
            
    public var body: some View {
        ZStack {
            VStack(spacing: 0) {
                Color.blue2
                    .frame(maxWidth: .infinity, maxHeight: 70)
                CustomCalendarView(selectedDate: $selectedDate)
                    .padding(.bottom, 20)
                DayTimelineView(selectedDate: selectedDate,
                                events: events,
                                onEventTap: { event in
                                    withAnimation(.spring(response: 0.4, dampingFraction: 0.85)) {
                                        selectedEvent = event
                                    }
                                },
                                onEventGroupTap: { group in
                                    withAnimation(.spring(response: 0.4, dampingFraction: 0.85)) {
                                        selectedEventGroup = group
                                    }
                                }
                )
                Spacer()
                ZStack {
                    VStack(spacing: 0) {
                        Color.navy
                            .frame(maxWidth: .infinity, maxHeight: 20)
                        Color.blue2
                            .frame(maxWidth: .infinity, maxHeight: 125)
                    }
                    .offset(y: bottomOffset)
                    HStack(alignment: .center, spacing : 0) {
                        Circle()
                            .frame(width: 145, height: 145)
                            .foregroundColor(Color.white)
                            .offset(y: -70)
                    }
                    HStack(alignment: .center, spacing : 0) {
                        VStack {
                            Button {
                                viewModel.activeSheet = .addEvent
                            } label: {
                                ZStack {
                                    Image(systemName: "plus")
                                        .frame(width: 110, height: 110)
                                        .foregroundColor(Color.white)
                                        .font(.system(size: 60, weight: .regular))
                                        .background(Color.blue2)
                                        .clipShape(Circle())
                                        .overlay {
                                            Circle()
                                                .stroke(Color.navy, lineWidth: 10)
                                        }
                                        .shadow(radius: 30)
                                }
                            }
                        }
                        .offset(y: -70)
                    }
                    .opacity(contentOpacity)
                    HStack(spacing: 0) {
                        RoundButton(imageName: "list.bullet", background: .navy) {
                            showMenu.toggle()
                        }
                        .padding(.leading, 40)
                        Spacer()
                        RoundButton(imageName: "list.clipboard", background: .navy) {
                            viewModel.activeSheet = .eventsList
                        }
                        .padding(.trailing, 40)
                    }
                    .opacity(contentOpacity)
                }
                .onAppear {
                    withAnimation(.easeOut(duration: 0.5)) {
                        bottomOffset = 0
                    }
                    withAnimation(.easeOut(duration: 0.5).delay(0.6)) {
                        contentOpacity = 1.0
                    }
                }
            }
            .ignoresSafeArea(.container, edges: [.all])
            .sheet(item: $viewModel.activeSheet) { sheet in
                switch sheet {
                case .addEvent:
                    AddEventView()
                case .eventsList:
                    EventsListView(userId: idModel.currentUserId, showMenu: $showMenu, selectedDate: viewModel.selectedDate)
                case .settingsView:
                    EventsSettingsView()
                }
            }
            .ignoresSafeArea(.container, edges: [.all])
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

                EventDetailCard(userId: idModel.currentUserId, event: event, isGroupEvent: false, onDelete: {
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
            if let events = selectedEventGroup {
                Color.black
                    .opacity(0.45)
                    .ignoresSafeArea()
                    .onTapGesture {
                        withAnimation(.spring(
                            response: 0.4,
                            dampingFraction: 0.85
                        )) {
                            selectedEventGroup = nil
                        }
                    }
                EventGroupDetailView(
                    events: events,
                    onDismiss: {
                        withAnimation(.spring(
                            response: 0.4,
                            dampingFraction: 0.85
                        )) {
                            selectedEventGroup = nil
                        }
                    }
                )
                .padding(.all, 30)
                .transition(
                    .scale(scale: 0.85)
                    .combined(with: .opacity)
                )
                .zIndex(11)
            }
        }
        .background(.white)
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
    
    struct DayTimelineView: View {
        let selectedDate: Date
        let events: [EventItem]
        let onEventTap: (EventItem) -> Void
        let onEventGroupTap: ([EventItem]) -> Void

        private let calendar = Calendar.current

        private let hourHeight: CGFloat = 100
        private let timelineLeading: CGFloat = 75
        private let timelineTrailing: CGFloat = 20
        private let eventSpacing: CGFloat = 5

        private var dayEvents: [EventItem] {
            events.filter {
                calendar.isDate(
                    $0.startDate.dateValue(),
                    inSameDayAs: selectedDate
                )
            }
        }

        private struct PositionedEvent: Identifiable {
            let event: EventItem
            let column: Int
            let totalColumns: Int
            let groupId: Int

            var id: String {
                event.id ?? UUID().uuidString
            }
        }
        
        private var eventGroups: [[EventItem]] {
            let sortedEvents = dayEvents.sorted {
                $0.startDate.dateValue() < $1.startDate.dateValue()
            }

            var groups: [[EventItem]] = []
            var currentGroup: [EventItem] = []
            var currentGroupEnd: Date?

            for event in sortedEvents {
                let start = event.startDate.dateValue()
                let end = event.endDate.dateValue()

                if let groupEnd = currentGroupEnd {
                    if start < groupEnd {
                        currentGroup.append(event)
                        currentGroupEnd = max(groupEnd, end)
                    } else {
                        groups.append(currentGroup)

                        currentGroup = [event]
                        currentGroupEnd = end
                    }
                } else {
                    currentGroup = [event]
                    currentGroupEnd = end
                }
            }

            if !currentGroup.isEmpty {
                groups.append(currentGroup)
            }

            return groups
        }

        private var positionedEvents: [PositionedEvent] {
            var positioned: [PositionedEvent] = []

            for (groupIndex, group) in eventGroups.enumerated() {
                var columns: [[EventItem]] = []

                for event in group {
                    let start = event.startDate.dateValue()
                    var placed = false

                    for columnIndex in columns.indices {
                        if let lastEvent = columns[columnIndex].last {
                            let lastEnd =
                                lastEvent.endDate.dateValue()

                            if start >= lastEnd {
                                columns[columnIndex].append(event)
                                placed = true
                                break
                            }
                        }
                    }

                    if !placed {
                        columns.append([event])
                    }
                }

                let totalColumns = columns.count

                for columnIndex in columns.indices {

                    for event in columns[columnIndex] {

                        positioned.append(
                            PositionedEvent(
                                event: event,
                                column: columnIndex,
                                totalColumns: totalColumns,
                                groupId: groupIndex
                            )
                        )
                    }
                }
            }

            return positioned
        }

        var body: some View {
            GeometryReader { geometry in
                ScrollView(.vertical, showsIndicators: true) {
                    ZStack(alignment: .topLeading) {

                        // Timeline hours
                        VStack(spacing: 0) {
                            ForEach(0..<24, id: \.self) { hour in
                                TimelineHourRow(hour: hour)
                                    .frame(height: hourHeight)
                            }
                        }

                        // Events
                        ForEach(positionedEvents) { positionedEvent in
                            if positionedEvent.column < 2 {
                                let event = positionedEvent.event
                                let start = event.startDate.dateValue()
                                
                                let startMinutes =
                                calendar.component(.hour, from: start) * 60 +
                                calendar.component(.minute, from: start)
                                
                                let yOffset =
                                CGFloat(Double(startMinutes) * (hourHeight / 60.0))
                                
                                let width = eventWidth(
                                    for: positionedEvent,
                                    availableWidth: geometry.size.width
                                )
                                
                                TimelineEventView(event: event, cardWidth: width)
                                    .frame(
                                        width: eventWidth(
                                            for: positionedEvent,
                                            availableWidth: geometry.size.width
                                        )
                                    )
                                    .offset(
                                        x: eventXOffset(
                                            for: positionedEvent,
                                            availableWidth: geometry.size.width
                                        ),
                                        y: yOffset + 50
                                    )
                                    .onTapGesture {
                                        onEventTap(event)
                                    }
                            }
                        }
                        
                        ForEach(0..<eventGroups.count, id: \.self) { groupIndex in
                            let groupEvents = positionedEvents(
                                inGroup: groupIndex
                            )

                            // Only show a "+ more" card if the group actually
                            // uses more than 2 columns.
                            if let firstPositionedEvent = groupEvents.first,
                               firstPositionedEvent.totalColumns > 2 {

                                let firstEvent = firstPositionedEvent.event

                                let start = firstEvent.startDate.dateValue()

                                let startMinutes =
                                    calendar.component(.hour, from: start) * 60 +
                                    calendar.component(.minute, from: start)

                                let yOffset =
                                    CGFloat(startMinutes) * hourHeight / 60

                                let moreCount = groupEvents.filter {
                                    $0.column >= 2
                                }.count

                                let morePositionedEvent = PositionedEvent(
                                    event: firstEvent,
                                    column: 2,
                                    totalColumns: firstPositionedEvent.totalColumns,
                                    groupId: groupIndex
                                )

                                MoreEventsCard(count: moreCount) {
                                    onEventGroupTap(
                                        groupEvents.map { $0.event }
                                    )
                                }
                                .frame(
                                    width: eventWidth(
                                        for: morePositionedEvent,
                                        availableWidth: geometry.size.width
                                    ),
                                    height: hourHeight / 2
                                )
                                .offset(
                                    x: eventXOffset(
                                        for: morePositionedEvent,
                                        availableWidth: geometry.size.width
                                    ),
                                    y: yOffset + 50
                                )
                            }
                        }
                    }
                    .frame(width: geometry.size.width, height: hourHeight * 24, alignment: .topLeading)
                }
            }
        }

        private func eventWidth(for positionedEvent: PositionedEvent, availableWidth: CGFloat) -> CGFloat {
            let usableWidth = availableWidth - timelineLeading - timelineTrailing
            let spacing = eventSpacing * CGFloat(positionedEvent.totalColumns - 1)

            if positionedEvent.totalColumns < 4 {
                return (usableWidth - spacing) / CGFloat(positionedEvent.totalColumns)
            } else {
                return (usableWidth - spacing) / CGFloat(3.0)
            }
        }

        private func eventXOffset(for positionedEvent: PositionedEvent, availableWidth: CGFloat) -> CGFloat {
            let width = eventWidth(
                for: positionedEvent,
                availableWidth: availableWidth
            )

            return timelineLeading
                + CGFloat(positionedEvent.column)
                * (width + eventSpacing)
        }
        
        private func groupColumnCount(_ group: [EventItem]) -> Int {

            var columns: [[EventItem]] = []

            for event in group {
                let start = event.startDate.dateValue()
                var placed = false

                for columnIndex in 0..<columns.count {
                    if let lastEvent = columns[columnIndex].last {
                        let lastEnd = lastEvent.endDate.dateValue()

                        if start >= lastEnd {
                            columns[columnIndex].append(event)
                            placed = true
                            break
                        }
                    }
                }

                if !placed {
                    columns.append([event])
                }
            }

            return columns.count
        }
        
        private func positionedEvents(inGroup groupIndex: Int) -> [PositionedEvent] {
            positionedEvents.filter {
                $0.groupId == groupIndex
            }
        }
    }
    
    struct TimelineHourRow: View {
        let hour: Int

        private let hourHeight: CGFloat = 100

        var body: some View {
            ZStack(alignment: .topLeading) {
                HStack(spacing: 0) {
                    Text(hourString)
                        .font(.custom("ARIAL", size: 12))
                        .foregroundColor(Color.navy)
                        .frame(width: 55, alignment: .trailing)
                        .padding(.trailing, 10)
                    Rectangle()
                        .fill(Color.navy)
                        .frame(height: 1)
                }
                .offset(y: 0)
            }
        }

        private var hourString: String {
            let calendar = Calendar.current

            var components = DateComponents()
            components.hour = hour

            let date = calendar.date(from: components) ?? Date()

            let formatter = DateFormatter()
            formatter.dateFormat = "h a"

            return formatter.string(from: date)
        }
    }
    
    struct TimelineEventView: View {
        let event: EventItem
        let cardWidth: CGFloat
        
        private let calendar = Calendar.current
        private let hourHeight: CGFloat = 100

        private var start: Date {
            event.startDate.dateValue()
        }

        private var end: Date {
            event.endDate.dateValue()
        }
        
        private var eventHeight: CGFloat {
            let duration = end.timeIntervalSince(start)
            return max(CGFloat(duration / 3600) * hourHeight, 30/60 * hourHeight)
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
            HStack(spacing: 8) {
                RoundedRectangle(cornerRadius: 4)
                    .fill(Color.navy)
                    .frame(width: 4)
                VStack(alignment: .leading, spacing: 2) {
                    let duration = end.timeIntervalSince(start)

                    Text(event.title)
                        .font(.custom("ARIAL", size: 14))
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                        .lineLimit(2)
                        .padding(.top, 10)
                    if duration >= 2400 && cardWidth >= 150 {
                        HStack(alignment: .center, spacing: 0) {
                            Text("From: ")
                                .font(.custom("ARIAL", size: 11))
                                .foregroundColor(.white)
                            Text(event.startDate.dateValue(), style: .time)
                                .font(.custom("ARIAL", size: 11))
                                .foregroundColor(.white)
                                .padding(.trailing, 10)
                        }
                        HStack(alignment: .center, spacing: 0) {
                            Text("To: ")
                                .font(.custom("ARIAL", size: 11))
                                .foregroundColor(.white)
                            Text(event.endDate.dateValue(), style: .time)
                                .font(.custom("ARIAL", size: 11))
                                .foregroundColor(.white)
                        }
                    }
                    
                    Spacer()
                }
                Spacer()
            }
            .padding(.horizontal, 10)
            .padding(.vertical, 7)
            .background(eventColor)
            .frame(height: eventHeight, alignment: .top)
            .clipShape(
                RoundedRectangle(cornerRadius: 8)
            )
        }
    }
}
