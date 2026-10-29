package com.eaglenews.app.ui.screens.todo

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.eaglenews.app.models.ToDoListItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.UUID
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/** Equivalent of Swift's `ActiveAlert` enum used by both New/Edit view models. */
enum class ActiveAlert { ERROR, SUCCESS }

/** Equivalent of SwiftUI's NewItemViewViewModel. */
class NewItemViewModel : ViewModel() {
    var title by mutableStateOf("")
    var dueDate by mutableStateOf(Date())
    var activeAlert by mutableStateOf<ActiveAlert?>(null)
    var errorMessage by mutableStateOf("")

    // Ensures all fields are filled - mirrors `canSave`
    private val canSave: Boolean
        get() {
            if (title.trim().isEmpty()) {
                errorMessage = "Please fill in all fields."
                return false
            }
            // Deals with time zones; 86_400_000 ms is a day's worth
            if (dueDate.time < Date().time - 86_400_000L) {
                errorMessage = "Please enter a valid date."
                return false
            }
            return true
        }

    fun save(onComplete: (Boolean) -> Unit) {
        if (!canSave) {
            onComplete(false)
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            onComplete(false)
            return
        }

        val newId = UUID.randomUUID().toString()
        val newItem = ToDoListItem(
            id = newId,
            title = title,
            dueDate = dueDate.time / 1000.0,
            createdDate = Date().time / 1000.0,
            reminderSent = false,
            isDone = false,
            pinned = false
        )

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("todos")
            .document(newId)
            .set(newItem.toMap())
            .addOnCompleteListener { task -> onComplete(task.isSuccessful) }

        errorMessage = ""
    }
}