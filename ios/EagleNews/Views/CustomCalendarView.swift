import SwiftUI
import FirebaseCore
import FirebaseFirestore
import FirebaseAuth

struct CustomCalendarView: View {
    @Binding var selectedDate: Date
    @State var events: [EventItem] = []
    @State private var displayedMonth = Date()
    @State private var showingMonthPicker = false
    
    private let calendar = Calendar.current
    
    @State private var topOffset: CGFloat = -300
    @State private var calendarOpacity: Double = 0

    private let columns = Array(
        repeating: GridItem(.flexible(), spacing: 0),
        count: 7
    )

    var body: some View {
        ZStack(alignment: .top) {
            VStack(spacing: 0) {
                ZStack {
                    Color.navy
                        .frame(maxWidth: .infinity, maxHeight: 315)
                        .clipShape(
                            UnevenRoundedRectangle(
                                topLeadingRadius: 0,
                                bottomLeadingRadius: 35,
                                bottomTrailingRadius: 35,
                                topTrailingRadius: 0
                            )
                        )
                        .offset(y: topOffset + 10)
                    Color.blue2
                        .frame(maxWidth: .infinity, maxHeight: 315)
                        .clipShape(
                            UnevenRoundedRectangle(
                                topLeadingRadius: 0,
                                bottomLeadingRadius: 35,
                                bottomTrailingRadius: 35,
                                topTrailingRadius: 0
                            )
                        )
                        .offset(y: topOffset)
                }
                .onAppear {
                    withAnimation(.easeOut(duration: 0.5)) {
                        topOffset = 0
                    }
                }
            }
            VStack(spacing: 0) {
                HStack {
                    Button {
                        withAnimation {
                            displayedMonth = calendar.date(
                                byAdding: .month,
                                value: -1,
                                to: displayedMonth
                            )!
                        }
                    } label: {
                        Image(systemName: "chevron.left")
                            .foregroundColor(.white)
                    }
                    
                    Spacer()
                    
                    Button {
                        if !showingMonthPicker {
                            showingMonthPicker = true
                        } else {
                            showingMonthPicker = false
                        }
                    } label: {
                        Text(displayedMonth.formatted(
                            .dateTime
                                .month(.wide)
                                .year()
                        ))
                        .font(.custom("ARIAL", size: 15))
                        .foregroundColor(.white)
                    }
                    
                    Spacer()
                    
                    Button {
                        withAnimation {
                            displayedMonth = calendar.date(
                                byAdding: .month,
                                value: 1,
                                to: displayedMonth
                            )!
                        }
                    } label: {
                        Image(systemName: "chevron.right")
                            .foregroundColor(.white)

                    }
                }
                .padding(.horizontal, 25)
                .padding(.bottom, 12)
                
                LazyVGrid(columns: columns, spacing: 4) {
                    ForEach(
                        calendar.shortStandaloneWeekdaySymbols,
                        id: \.self
                    ) { day in
                        Text(day)
                            .font(.caption)
                            .foregroundColor(Color.white)
                            .frame(height: 20)
                    }
                }
                .padding(.horizontal, 10)
                
                LazyVGrid(columns: columns, spacing: 2) {
                    ForEach(daysInMonth, id: \.self) { date in
                            CalendarDayView(
                                date: date,
                                displayedMonth: displayedMonth,
                                selectedDate: selectedDate,
                                hasEvent: hasEvent(on: date)
                            ) {
                                selectedDate = date
                            }
                            .padding(.bottom, 5)
                        }
                }
                .padding(.horizontal, 10)
                .padding(.bottom, 10)
                
            }
            .opacity(calendarOpacity)
            .onAppear {
                withAnimation(.easeOut(duration: 0.5).delay(0.6)) {
                    calendarOpacity = 1.0
                }
            }
            .ignoresSafeArea(.container, edges: [.all])
            
            if showingMonthPicker {
                MonthYearPicker(
                    displayedMonth: $displayedMonth
                )
                .frame(width: 250, height: 125)
                .background(Color.white)
                .clipShape(
                    RoundedRectangle(cornerRadius: 12)
                )
                .shadow(color: .black.opacity(0.2), radius: 10, x: 0, y: 5)
                .offset(y: 55)
                .zIndex(10)
            }
        }
        .onAppear {
            if let userId = Auth.auth().currentUser?.uid {
                loadEvents(userId: userId)
            }
        }
    }

    private var daysInMonth: [Date] {
        guard let monthInterval = calendar.dateInterval(
            of: .month,
            for: displayedMonth
        ) else {
            return []
        }

        let firstDay = monthInterval.start

        let firstWeekday = calendar.component(
            .weekday,
            from: firstDay
        )

        let leadingDays =
            (firstWeekday - calendar.firstWeekday + 7) % 7

        var dates: [Date] = []

        // Previous month's days
        for offset in stride(
            from: leadingDays,
            through: 1,
            by: -1
        ) {
            if let date = calendar.date(
                byAdding: .day,
                value: -offset,
                to: firstDay
            ) {
                dates.append(date)
            }
        }

        // Current month's days
        let numberOfDays = calendar.range(
            of: .day,
            in: .month,
            for: displayedMonth
        )!.count

        for day in 1...numberOfDays {
            if let date = calendar.date(
                byAdding: .day,
                value: day - 1,
                to: firstDay
            ) {
                dates.append(date)
            }
        }

        // Next month's days to fill the final week
        let remainingDays = (7 - dates.count % 7) % 7

        if remainingDays > 0 {
            let lastDate = dates.last!

            for offset in 1...remainingDays {
                if let date = calendar.date(
                    byAdding: .day,
                    value: offset,
                    to: lastDate
                ) {
                    dates.append(date)
                }
            }
        }

        return dates
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
                }
            }
    }

    private func hasEvent(on date: Date) -> Bool {
        events.contains { event in
            calendar.isDate(
                event.startDate.dateValue(),
                inSameDayAs: date
            )
        }
    }
}

struct CalendarDayView: View {
    let date: Date
    let displayedMonth: Date
    let selectedDate: Date
    let hasEvent: Bool
    let action: () -> Void

    private let calendar = Calendar.current

    var isSelected: Bool {
        calendar.isDate(date, inSameDayAs: selectedDate)
    }
    
    var isToday: Bool {
        calendar.isDate(date, inSameDayAs: Date())
    }
    
    var isCurrentMonth: Bool {
        calendar.component(.month, from: date) ==
            calendar.component(.month, from: displayedMonth) &&
        calendar.component(.year, from: date) ==
            calendar.component(.year, from: displayedMonth)
    }

    var body: some View {
        Button(action: action) {
            VStack(spacing: 4) {
                Text("\(calendar.component(.day, from: date))")
                    .font(.system(size: 15))
                    .foregroundColor(Color.white)
                    .frame(width: 32, height: 32)
                    .background(
                        Circle()
                            .fill(isSelected ? Color.navy: Color.clear)
                    )
                    .overlay {
                        if isToday {
                            Circle()
                                .stroke(Color.navy, lineWidth: 2)
                        }
                    }
                Circle()
                    .fill(Color.navy)
                    .frame(width: 4, height: 4)
                    .opacity(hasEvent ? 1 : 0)
            }
            .opacity(isCurrentMonth ? 1.0 : 0.4)
            .frame(height: 38)
        }
        .buttonStyle(.plain)
    }
}
