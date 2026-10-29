package com.eaglenews.app.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ContentViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var currentUserId by mutableStateOf("")
        private set

    var isAdmin by mutableStateOf(false)
        private set

    var isClubLeader by mutableStateOf(false)
        private set

    val isSignedIn: Boolean
        get() = currentUserId.isNotEmpty()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            currentUserId = user?.uid ?: ""

            if (user != null) {
                testAdmin()
                testClubLeader()
            } else {
                isAdmin = false
                isClubLeader = false
            }
        }
    }

    fun logout() {
        auth.signOut()
        currentUserId = ""
        isAdmin = false
        isClubLeader = false
    }

    private fun testAdmin() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val roles = doc.get("activeRoles") as? List<*> ?: emptyList<Any>()
                isAdmin = roles.contains("admin")
            }
            .addOnFailureListener {
                isAdmin = false
            }
    }

    private fun testClubLeader() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val roles = doc.get("activeRoles") as? List<*> ?: emptyList<Any>()
                isClubLeader = roles.contains("clubLeader")
            }
            .addOnFailureListener {
                isClubLeader = false
            }
    }
}