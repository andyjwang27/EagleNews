package com.eaglenews.app.ui.screens.todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eaglenews.app.models.ToDoListItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

/** Equivalent of SwiftUI's EditToDoViewViewModel. */
class EditToDoViewModel(selectedToDo: ToDoListItem) : ViewModel() {
    private val todoId = selectedToDo.id

    var title by mutableStateOf(selectedToDo.title)
    var dueDate by mutableStateOf(Date((selectedToDo.dueDate * 1000).toLong()))
    var activeAlert by mutableStateOf<ActiveAlert?>(null)
    var errorMessage by mutableStateOf("")

    private val canSave: Boolean
        get() {
            if (title.trim().isEmpty()) {
                errorMessage = "Please fill in all fields."
                return false
            }
            if (dueDate.time < Date().time - 86_400_000L) {
                errorMessage = "Please enter a valid date."
                return false
            }
            return true
        }

    fun update(onComplete: (Boolean) -> Unit) {
        if (!canSave) {
            onComplete(false)
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            onComplete(false)
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("todos")
            .document(todoId)
            .update(
                mapOf(
                    "title" to title,
                    "dueDate" to dueDate.time / 1000.0
                )
            )
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    errorMessage = task.exception?.localizedMessage ?: "Unknown error"
                }
                onComplete(task.isSuccessful)
            }

        errorMessage = ""
    }

    class Factory(private val selectedToDo: ToDoListItem) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return EditToDoViewModel(selectedToDo) as T
        }
    }
}