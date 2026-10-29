import SwiftUI
import FirebaseFirestore

struct ToDoWeekView: View {
    @StateObject var viewModel: ToDoListViewViewModel
    @Binding var showMenu: Bool
    @State private var itemToDelete: ToDoListItem?
    @State var selectedToDo: ToDoListItem?
    @State var todoToDelete: ToDoListItem?
    @FirestoreQuery var items: [ToDoListItem]
    
    private let userId: String
    
    init (userId: String, showMenu: Binding<Bool>) {
        self.userId = userId
        self._showMenu = showMenu
        self._items = FirestoreQuery(collectionPath: "users/\(userId)/todos")
        self._viewModel = StateObject(wrappedValue: ToDoListViewViewModel(userId: userId))
    }
    
    // Items for this week that are completed
    var completedItems: [ToDoListItem] {
        let calendar = Calendar.current
        let startOfToday = calendar.startOfDay(for: Date())
        let oneWeekFromToday = calendar.date(byAdding: .day, value: 7, to: startOfToday)!

        let completed = items.filter { item in
            let itemDate = Date(timeIntervalSince1970: item.dueDate)

            return item.isDone && ((itemDate >= startOfToday && itemDate < oneWeekFromToday) || itemDate < startOfToday)
        }

        return sortPinnedAndDueDate(completed)
    }
    
    private var completedIds: Set<String> {
        Set(completedItems.map(\.id))
    }
    
    // Items for this week
    var weekItems: [ToDoListItem] {
        let calendar = Calendar.current
        let startOfToday = calendar.startOfDay(for: Date())
        let oneWeekFromToday = calendar.date(byAdding: .day, value: 7, to: startOfToday)!
        
        let itemsForWeek = items.filter { item in
            let itemDate = Date(timeIntervalSince1970: item.dueDate)
            
            return itemDate >= startOfToday && itemDate < oneWeekFromToday && !completedIds.contains(item.id)
        }
        
        return sortPinnedAndDueDate(itemsForWeek)
    }
    
    // Items that are overdue
    var overDueItems: [ToDoListItem] {
        let calendar = Calendar.current
        let startOfToday = calendar.startOfDay(for: Date())
        
        let itemsOverdue = items.filter { item in
            let itemDate = Date(timeIntervalSince1970: item.dueDate)
            return itemDate < startOfToday && !completedIds.contains(item.id)
        }
        
        return sortPinnedAndDueDate(itemsOverdue)
    }
    
    public var body: some View {
        ZStack {
            DefaultView(title: "To-Do List", showMenu: $showMenu, showMenuButton: true) {
                RoundButton(imageName: "plus", background: .navy) {
                    viewModel.todoSheet = .newItem
                }
            }
            List {
                if !weekItems.isEmpty {
                    ToDoSectionView(
                        sectionTitle: "This Week's Items",
                        items: weekItems,
                        onEdit: { item in
                            selectedToDo = item
                            viewModel.todoSheet = .editToDo
                        },
                        onTogglePin: { item in
                            viewModel.togglePin(item: item)
                        },
                        onDelete: { item in
                            todoToDelete = item
                        }
                    )
                }
                
                if !overDueItems.isEmpty {
                    ToDoSectionView(
                        sectionTitle: "Overdue",
                        items: overDueItems,
                        onEdit: { item in
                            selectedToDo = item
                            viewModel.todoSheet = .editToDo
                        },
                        onTogglePin: { item in
                            viewModel.togglePin(item: item)
                        },
                        onDelete: { item in
                            todoToDelete = item
                        }
                    )
                }
                
                if !completedItems.isEmpty {
                    ToDoSectionView(
                        sectionTitle: "Completed",
                        items: completedItems,
                        showCompletedStyle: true,
                        onEdit: { _ in },
                        onTogglePin: { _ in },
                        onDelete: { item in
                            todoToDelete = item
                        }
                    )
                }
            }
            .padding(.top, 150)
            .padding(.bottom, 150)
            .ignoresSafeArea(.container, edges: [.top, .bottom])
            .alert("Delete Todo?", isPresented: Binding(
                get: { todoToDelete != nil },
                set: { if !$0 { todoToDelete = nil } }
            )) {
                Button("Delete", role: .destructive) {
                    if let todo = todoToDelete {
                        viewModel.delete(id: todo.id)
                    }
                    todoToDelete = nil
                }

                Button("Cancel", role: .cancel) {
                    todoToDelete = nil
                }
            } message: {
                Text("This action cannot be undone.")
            }
            .sheet(item: $viewModel.todoSheet) { sheet in
                switch sheet {
                case .newItem:
                    NewItemView()
                case .editToDo:
                    if let item = selectedToDo {
                            EditToDoView(
                                selectedToDo: item,
                                showMenu: $showMenu
                            )
                        } else {
                            Text("No item selected")
                        }
                }
            }
            .animation(.spring(response: 0.4, dampingFraction: 0.8), value: completedItems.count)
            .animation(.spring(response: 0.4, dampingFraction: 0.8), value: overDueItems.count)
            .animation(.spring(response: 0.4, dampingFraction: 0.8), value: weekItems.count)
        }
    }
}

private func sortPinnedAndDueDate(_ items: [ToDoListItem]) -> [ToDoListItem] {
    items.sorted {
        if $0.pinned != $1.pinned {
            return $0.pinned
        }
        return $0.dueDate < $1.dueDate
    }
}
