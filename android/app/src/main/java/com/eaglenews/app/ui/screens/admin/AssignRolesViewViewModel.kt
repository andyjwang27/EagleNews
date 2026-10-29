package com.eaglenews.app.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eaglenews.app.models.User
import com.eaglenews.app.ui.screens.todo.ActiveAlert
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AssignRolesViewViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    var selectedUsers: Set<User> = emptySet()
    var selectedRoles: List<String> = emptyList()
    var activeAlert by mutableStateOf<ActiveAlert?>(null)
        private set

    var errorMessage by mutableStateOf("")
        private set

    var didSucceed by mutableStateOf(false)
        private set

    fun assign() {
        if(!validate()) {
            activeAlert = ActiveAlert.ERROR
            return
        }

        var completed = 0
        var failed = false

        selectedUsers.forEach { user ->
            val userRef =
                db.collection("users")
                    .document(user.id)
            userRef.update(
                "activeRoles",
                FieldValue.arrayUnion(
                    *selectedRoles.toTypedArray()
                )
            )
                .addOnSuccessListener {
                    completed++
                    if(completed == selectedUsers.size) {
                        didSucceed = true
                        activeAlert = ActiveAlert.SUCCESS
                    }
                }
                .addOnFailureListener { error ->
                    failed = true
                    errorMessage =
                        error.message
                            ?: "Unable to assign roles."
                    activeAlert =
                        ActiveAlert.ERROR
                }
        }
    }
    private fun validate(): Boolean {
        if(selectedUsers.isEmpty()) {
            errorMessage =
                "Please select at least one user."
            return false
        }

        if(selectedRoles.isEmpty()) {
            errorMessage =
                "Please select at least one role."
            return false

        }
        return true
    }

    fun clearAlert() {
        activeAlert = null
    }

}