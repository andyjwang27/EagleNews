import Foundation
internal import Combine
import FirebaseFirestore
import FirebaseAuth

class EditEventViewViewModel: ObservableObject {
    @Published var title = ""
    @Published var startTime: Date
    @Published var endTime: Date
    @Published var selectedDate: Date = Date()
    @Published var activeAlert: ActiveAlert?
    @Published var selectedColor: String
    @Published var errorMessage = ""
    @Published var roleNames: [String]
    @Published var selectedUsers: Set<User> = []
    @Published var isGroup: Bool
    @Published var recipientIds: [String]
    @Published var publisher: String
    @Published var publisherId: String
    @Published var eventId: String
    @Published var chosenTime = Date()
    @Published var eventMinutesBefore: Int
    
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
    
    init(event: EventItem) {
        self.eventId = event.groupEventId ?? ""
        self.title = event.title
        self.selectedDate = event.startDate.dateValue()
        self.startTime = event.startDate.dateValue()
        self.endTime = event.endDate.dateValue()
        self.selectedColor = event.color
        self.roleNames = event.tags
        self.isGroup = event.isGroup
        self.recipientIds = event.recipientIds
        self.publisher = event.publisher
        self.publisherId = event.publisherId
        self.eventMinutesBefore = event.eventMinutesBefore
        
        loadSelectedUsers()
    }

    var canSave: Bool {
        guard !title.trimmingCharacters(in: .whitespaces).isEmpty else {
            errorMessage = "Please fill in all fields."
            return false
        }
        return true
    }
    
    func updateEvent() {
        guard canSave else {
            activeAlert = .error
            return
        }

        guard let uid = Auth.auth().currentUser?.uid else {
            errorMessage = "Unable to fetch user."
            activeAlert = .error
            return
        }

        let db = Firestore.firestore()

        // Non-group events only belong to the current user.
        guard isGroup else {
            let ref = db.collection("users")
                .document(uid)
                .collection("events")
                .document(eventId)

            ref.updateData([
                "title": title,
                "startDate": startTime,
                "endDate": endTime,
                "color": selectedColor,
                "tags": [],
                "recipientIds": [],
                "reminderSent": false
            ]) { [weak self] error in
                DispatchQueue.main.async {
                    if let error {
                        self?.errorMessage = error.localizedDescription
                        self?.activeAlert = .error
                    } else {
                        self?.activeAlert = .success
                    }
                }
            }

            return
        }

        // First, get users selected directly.
        var newRecipientIds = Set(
            selectedUsers.compactMap(\.id)
        )

        // If there are no roles, we can immediately update.
        guard !roleNames.isEmpty else {
            updateGroupRecipients(
                db: db,
                newRecipientIds: newRecipientIds
            )
            return
        }

        // Add every user who has one of the selected roles.
        db.collection("users")
            .whereField("activeRoles", arrayContainsAny: roleNames)
            .getDocuments { [weak self] snapshot, error in

                guard let self else { return }

                if let error {
                    DispatchQueue.main.async {
                        self.errorMessage = error.localizedDescription
                        self.activeAlert = .error
                    }
                    return
                }

                snapshot?.documents.forEach { document in
                    newRecipientIds.insert(document.documentID)
                }

                self.updateGroupRecipients(
                    db: db,
                    newRecipientIds: newRecipientIds
                )
            }
    }
    
    func loadSelectedUsers() {
        guard !recipientIds.isEmpty else { return }

        let db = Firestore.firestore()

        db.collection("users")
            .whereField(FieldPath.documentID(), in: recipientIds)
            .getDocuments { [weak self] snapshot, error in

                guard let self = self,
                      error == nil,
                      let documents = snapshot?.documents else {
                    return
                }

                Task { @MainActor in
                    let users = documents.compactMap { document -> User? in
                        try? document.data(as: User.self)
                    }

                    self.selectedUsers = Set(users)
                }
            }
    }
    
    private func updateGroupRecipients(
        db: Firestore,
        newRecipientIds: Set<String>
    ) {
        let oldRecipientIds = Set(recipientIds)

        let usersToRemove = oldRecipientIds.subtracting(newRecipientIds)
        let usersToAdd = newRecipientIds.subtracting(oldRecipientIds)
        let usersToUpdate = oldRecipientIds.intersection(newRecipientIds)

        let updatedData: [String: Any] = [
            "title": title,
            "startDate": Timestamp(date: startTime),
            "endDate": Timestamp(date: endTime),
            "color": selectedColor,
            "tags": roleNames,
            "isGroup": true,
            "recipientIds": Array(newRecipientIds),
            "reminderSent": false
        ]
        
        let event = EventItem(
            title: title,
            startDate: Timestamp(date: startTime),
            endDate: Timestamp(date: endTime),
            pinned: false,
            publisher: publisher,
            publisherId: publisherId,
            tags: roleNames,
            isGroup: true,
            color: selectedColor,
            reminderSent: false,
            recipientIds: Array(newRecipientIds),
            groupEventId: eventId,
            eventMinutesBefore: eventMinutesBefore
        )

        let group = DispatchGroup()

        let batch = db.batch()

        // Find the existing event for every user who keeps it.
        for userId in usersToUpdate {
            group.enter()

            db.collection("users")
                .document(userId)
                .collection("events")
                .whereField("groupEventId", isEqualTo: eventId)
                .getDocuments { snapshot, error in

                    defer {
                        group.leave()
                    }

                    guard error == nil else {
                        return
                    }

                    snapshot?.documents.forEach { document in
                        batch.updateData(
                            updatedData,
                            forDocument: document.reference
                        )
                    }
                }
        }

        // Find and delete the existing event for removed users.
        for userId in usersToRemove {
            group.enter()

            db.collection("users")
                .document(userId)
                .collection("events")
                .whereField("groupEventId", isEqualTo: eventId)
                .getDocuments { snapshot, error in

                    defer {
                        group.leave()
                    }

                    guard error == nil else {
                        return
                    }

                    snapshot?.documents.forEach { document in
                        batch.deleteDocument(document.reference)
                    }
                }
        }
        
        for userId in usersToAdd {
            let ref = db.collection("users")
                .document(userId)
                .collection("events")
                .document() // generates a NEW unique document ID

            do {
                try batch.setData(
                    from: event,
                    forDocument: ref
                )
            } catch {
                errorMessage = error.localizedDescription
                activeAlert = .error
                return
            }
        }

        group.notify(queue: .main) { [weak self] in
            batch.commit { error in
                if let error {
                    self?.errorMessage = error.localizedDescription
                    self?.activeAlert = .error
                } else {
                    self?.recipientIds = Array(newRecipientIds)
                    self?.activeAlert = .success
                }
            }
        }
    }
}
