package com.eaglenews.app.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eaglenews.app.ui.screens.todo.ActiveAlert
import com.google.firebase.firestore.FirebaseFirestore

class AddLinkViewViewModel : ViewModel() {

    private val db =
        FirebaseFirestore.getInstance()


    var errorMessage by mutableStateOf("")
        private set

    var activeAlert by mutableStateOf<ActiveAlert?>(null)
        private set


    fun addLink(
        name: String,
        link: String,
        tags: List<String>,
        onSuccess: () -> Unit
    ) {

        if(
            name.trim().isEmpty() ||
            link.trim().isEmpty() ||
            tags.isEmpty()
        ) {
            errorMessage =
                "Please fill in all fields"

            activeAlert =
                ActiveAlert.ERROR

            return
        }


        var formattedLink =
            link.trim()


        if(
            !formattedLink.lowercase()
                .startsWith("http://") &&
            !formattedLink.lowercase()
                .startsWith("https://")
        ) {
            formattedLink =
                "https://$formattedLink"
        }


        val data =
            hashMapOf(

                "name" to name.trim(),

                "link" to formattedLink,

                "tags" to tags

            )


        db.collection("links")
            .add(data)

            .addOnSuccessListener {

                activeAlert =
                    ActiveAlert.SUCCESS

                onSuccess()

            }

            .addOnFailureListener { error ->

                errorMessage =
                    error.message
                        ?: "Unable to save link."

                activeAlert =
                    ActiveAlert.ERROR

            }
    }


    fun clearAlert() {
        activeAlert = null
    }
}