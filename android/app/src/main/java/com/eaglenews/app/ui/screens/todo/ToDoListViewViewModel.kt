package com.eaglenews.app.ui.screens.todo

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eaglenews.app.models.ToDoListItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.Date
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

enum class ToDoSheet {
    NEW_ITEM,
    EDIT_TODO
}

class ToDoListViewModel(private val userId: String) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    private val _items = MutableStateFlow<List<ToDoListItem>>(emptyList())
    val items: StateFlow<List<ToDoListItem>> = _items.asStateFlow()

    // Mirrors @Published var todoSheet, and the @State selection/deletion vars
    var todoSheet by mutableStateOf<ToDoSheet?>(null)
    var selectedToDo by mutableStateOf<ToDoListItem?>(null)
    var todoToDelete by mutableStateOf<ToDoListItem?>(null)

    init {
        listenForItems()
    }

    private fun listenForItems() {
        listenerRegistration = db.collection("users")
            .document(userId)
            .collection("todos")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                _items.value = snapshot.documents.mapNotNull { ToDoListItem.fromDocument(it) }
            }
    }

    fun delete(id: String) {
        db.collection("users")
            .document(userId)
            .collection("todos")
            .document(id)
            .delete()
    }

    fun togglePin(item: ToDoListItem) {
        db.collection("users")
            .document(userId)
            .collection("todos")
            .document(item.id)
            .update("pinned", !item.pinned)
    }

    fun toggleCheck(item: ToDoListItem) {
        val markingDone = !item.isDone
        val docRef = db.collection("users")
            .document(userId)
            .collection("todos")
            .document(item.id)

        if (markingDone) {
            val itemDate = Date((item.dueDate * 1000).toLong())
            val startOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            if (itemDate.before(startOfDay)) {
                docRef.delete()
                return
            }
        }

        val updates = mutableMapOf<String, Any>("isDone" to markingDone)
        if (markingDone) updates["pinned"] = false
        docRef.update(updates)
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    class Factory(private val userId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ToDoListViewModel(userId) as T
        }
    }
}