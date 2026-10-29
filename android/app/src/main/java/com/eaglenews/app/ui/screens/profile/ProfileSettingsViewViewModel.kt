package com.eaglenews.app.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaglenews.app.ui.navigation.DeleteState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileSettingsViewViewModel : ViewModel() {

    var name by mutableStateOf("")
    var countdown by mutableStateOf(5)
    var deleteState by mutableStateOf(DeleteState.IDLE)

    fun save(onResult: (Boolean) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update("name", name)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun startDeleteFlow() {
        deleteState = DeleteState.CONFIRM_START
    }

    fun beginCountdown() {
        deleteState = DeleteState.COUNTING_DOWN
        countdown = 5

        viewModelScope.launch {
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            deleteState = DeleteState.READY_TO_DELETE
        }
    }

    fun cancelDelete() {
        deleteState = DeleteState.IDLE
        countdown = 5
    }

    fun deleteAccount(onResult: (Boolean) -> Unit = {}) {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        val uid = user.uid

        // 1. delete Firestore user doc (optional but recommended)
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .delete()

        // 2. delete auth account
        user.delete()
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

}