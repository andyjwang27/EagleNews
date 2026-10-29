package com.eaglenews.app.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaglenews.app.models.EventItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class EditEventViewModel(
    private val event: EventItem
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _title = MutableStateFlow(event.title)
    val title = _title.asStateFlow()

    private val _selectedDate =
        MutableStateFlow(event.date?.toDate() ?: Date())

    val selectedDate = _selectedDate.asStateFlow()


    private val _activeAlert = MutableStateFlow<EditAlert?>(null)
    val activeAlert = _activeAlert.asStateFlow()


    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()


    fun updateTitle(value: String) {
        _title.value = value
    }


    fun updateDate(date: Date) {
        _selectedDate.value = date
    }


    fun updateEvent() {

        if (_title.value.trim().isEmpty()) {
            _errorMessage.value = "Please fill in all fields."
            _activeAlert.value = EditAlert.ERROR
            return
        }


        val uid = auth.currentUser?.uid

        if (uid == null) {
            _errorMessage.value = "User not logged in."
            _activeAlert.value = EditAlert.ERROR
            return
        }


        db.collection("users")
            .document(uid)
            .collection("events")
            .document(event.id ?: "")
            .update(
                mapOf(
                    "title" to _title.value,
                    "date" to _selectedDate.value,
                    "reminderSent" to false
                )
            )
            .addOnSuccessListener {
                _activeAlert.value = EditAlert.SUCCESS
            }
            .addOnFailureListener {
                _errorMessage.value = it.message ?: "Unknown error"
                _activeAlert.value = EditAlert.ERROR
            }
    }


    fun clearAlert() {
        _activeAlert.value = null
    }
}


enum class EditAlert {
    ERROR,
    SUCCESS
}