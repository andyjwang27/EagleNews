package com.eaglenews.app.ui.screens.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eaglenews.app.models.EventItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Equivalent of SwiftUI's EventsListViewViewModel, plus the live query EventsListView needs. */
class EventsListViewModel(private val userId: String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    private val _items = MutableStateFlow<List<EventItem>>(emptyList())
    val items: StateFlow<List<EventItem>> = _items.asStateFlow()

    init {
        listenerRegistration = db.collection("users")
            .document(userId)
            .collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                _items.value = snapshot.documents.mapNotNull { document ->
                    document.toObject(EventItem::class.java)
                        ?.copy(id = document.id)
                }
            }
    }

    // Delete event
    fun delete(id: String) {
        db.collection("users")
            .document(userId)
            .collection("events")
            .document(id)
            .delete()
    }

    fun togglePin(event: EventItem) {
        val id = event.id ?: return

        db.collection("users")
            .document(userId)
            .collection("events")
            .document(id)
            .update("pinned", !event.pinned)
            .addOnFailureListener { error ->
                Log.e("EventsListViewModel", "Error updating pinned $error")
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    class Factory(private val userId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return EventsListViewModel(userId) as T
        }
    }
}