import SwiftUI
import Foundation
import FirebaseAuth
import FirebaseFirestore
internal import Combine

class AddEventViewViewModel: ObservableObject {
    @Published var title = ""
    @Published var startTime = Date()
    @Published var endTime = Date()
    @Published var reminderSent = false
    @Published var activeAlert: ActiveAlert?
    @Published var eventsByDate: [Date: [CalendarEvent]] = [:]
    @Published var selectedDate: Date = Date()
    @Published var selectedUsers: Set<User> = []
    @Published var errorMessage: String = ""
    @Published var tags: [String] = ["user"]
    @Published var selectedColor: String = "calendarOrange"
    @Published var publisher: String = ""
    @Published var chosenTime = Date()

    var eventMinutesBefore: Int {
        let components = Calendar.current.dateComponents([.hour, .minute], from: chosenTime)
        let hours = components.hour ?? 0
        let minutes = components.minute ?? 0
        return (hours * 60) + minutes
    }
    
    var displayTime: String {
        let totalMinutes = eventMinutesBefore
        if totalMinutes >= 60 {
            let minutes = totalMinutes % 60
            let hours = totalMinutes / 60
            return "\(hours)h \(minutes)m"
        } else {
            return String(eventMinutesBefore)
        }
    }
    
    func events(for date: Date) -> [CalendarEvent] {
        let day = Calendar.current.startOfDay(for: date)
        return eventsByDate[day] ?? []
    }
    
    func addEvent() {
        guard canSave else {
            self.activeAlert = .error
            return
        }
        
        // Get currentUserID
        guard let uId = Auth.auth().currentUser?.uid else {
            self.errorMessage = "Unable to fetch user."
            self.activeAlert = .error
            return
        }
        
        // Create model
        let calendar = Calendar.current
        let dateStartTime = calendar.date(
            bySettingHour: calendar.component(.hour, from: startTime),
            minute: calendar.component(.minute, from: startTime),
            second: 0,
            of: selectedDate
        )!
        let dateEndTime = calendar.date(
            bySettingHour: calendar.component(.hour, from: endTime),
            minute: calendar.component(.minute, from: endTime),
            second: 0,
            of: selectedDate
        )!
        
        let newEvent = EventItem(
            title: title,
            startDate: Timestamp(date: dateStartTime),
            endDate: Timestamp(date: dateEndTime),
            pinned: false,
            publisher: publisher,
            publisherId: uId,
            tags: tags,
            isGroup: false,
            color: selectedColor,
            reminderSent: reminderSent,
            recipientIds: [],
            groupEventId: "",
            eventMinutesBefore: eventMinutesBefore
        )
        
        // Save model to database
        let db = Firestore.firestore()
        
        do {
            try db.collection("users")
                .document(uId)
                .collection("events")
                .addDocument(from: newEvent)

            activeAlert = .success
            self.errorMessage = ""
        } catch {
            activeAlert = .error
        }
        
    }
    
    func addGroupEvent() {
        let groupEventId = UUID().uuidString
        
        guard canSave else {
            self.activeAlert = .error
            return
        }
        
        // Get currentUserID
        guard let uId = Auth.auth().currentUser?.uid else {
            self.errorMessage = "Unable to fetch user."
            self.activeAlert = .error
            return
        }
        
        let db = Firestore.firestore()
        
        let calendar = Calendar.current
        let dateStartTime = calendar.date(
            bySettingHour: calendar.component(.hour, from: startTime),
            minute: calendar.component(.minute, from: startTime),
            second: 0,
            of: selectedDate
        )!
        let dateEndTime = calendar.date(
            bySettingHour: calendar.component(.hour, from: endTime),
            minute: calendar.component(.minute, from: endTime),
            second: 0,
            of: selectedDate
        )!
        
        var targetUserIds = Set(selectedUsers.compactMap(\.id))
        
        let createEvents: () -> Void = {
            let event = EventItem(
                title: self.title,
                startDate: Timestamp(date: dateStartTime),
                endDate: Timestamp(date: dateEndTime),
                pinned: false,
                publisher: self.publisher,
                publisherId: uId,
                tags: self.tags,
                isGroup: true,
                color: self.selectedColor,
                reminderSent: false,
                recipientIds: Array(targetUserIds),
                groupEventId: groupEventId,
                eventMinutesBefore: self.eventMinutesBefore
            )
            
            let batch = db.batch()
            
            for userId in targetUserIds {
                let ref = db.collection("users")
                    .document(userId)
                    .collection("events")
                    .document()
                
                do {
                    try batch.setData(from: event, forDocument: ref)
                } catch {
                    self.errorMessage = error.localizedDescription
                    self.activeAlert = .error
                    return
                }
            }
            
            batch.commit { error in
                if let error {
                    self.errorMessage = error.localizedDescription
                    self.activeAlert = .error
                }
                
                db.collection("groupNotifications").addDocument(data: [
                    "title": self.title,
                    "publisher": self.publisher,
                    "publisherId": uId,
                    "recipientIds": Array(targetUserIds)
                ]) { error in
                    if let error {
                        print("Failed to create notification document:", error)
                    }
                    
                    DispatchQueue.main.async {
                        self.activeAlert = .success
                    }
                }
            }
        }
        
        if tags.isEmpty {
            createEvents()
            return
        }
        
        db.collection("users")
            .whereField("activeRoles", arrayContainsAny: tags)
            .getDocuments { snapshot, error in
                if let error {
                    self.errorMessage = error.localizedDescription
                    self.activeAlert = .error
                    return
                }
                
                snapshot?.documents.forEach { doc in
                    targetUserIds.insert(doc.documentID)
                }
                
                createEvents()
            }
    }
    
    func fetchUser() {
        guard let uid = Auth.auth().currentUser?.uid else {
            return
        }

        Firestore.firestore()
            .collection("users")
            .document(uid)
            .getDocument { snapshot, error in

                guard let data = snapshot?.data() else { return }

                let name = data["name"] as? String ?? ""

                DispatchQueue.main.async {
                    self.publisher = name
                }
            }
    }
    
    var canSave: Bool {
        guard !title.trimmingCharacters(in: .whitespaces).isEmpty else {
            self.errorMessage = "Please fill in all fields."
            self.activeAlert = .error
            return false
        }
        
        return true
    }
}
