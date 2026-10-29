import SwiftUI
internal import Combine

struct CalendarEvent: Identifiable, Hashable {
    let id = UUID()
    var title: String
    var time: Date
}

enum ActiveSheet: Identifiable {
    case addEvent
    case eventsList
    case settingsView

    var id: String {
        switch self {
        case .addEvent: return "addEvent"
        case .eventsList: return "eventsList"
        case .settingsView: return "settingsView"
        }
    }
}

class CalendarViewViewModel: ObservableObject {
    @Published var eventsByDate: [Date: [CalendarEvent]] = [:]
    @Published var selectedDate: Date = Date()
    @Published var activeSheet: ActiveSheet?
    
    func events(for date: Date) -> [CalendarEvent] {
        let day = Calendar.current.startOfDay(for: date)
        return eventsByDate[day] ?? []
    }
}
