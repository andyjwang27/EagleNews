package com.eaglenews.app.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eaglenews.app.models.User
import com.eaglenews.app.ui.screens.todo.ActiveAlert
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


class RemoveRolesViewViewModel : ViewModel() {


    private val db =
        FirebaseFirestore.getInstance()


    var errorMessage by mutableStateOf("")
        private set


    var successMessage by mutableStateOf("")
        private set


    var activeAlert by mutableStateOf<ActiveAlert?>(null)
        private set


    var didSucceed by mutableStateOf(false)
        private set



    fun removeRoles(
        from: User,
        rolesToDelete: List<String>
    ) {


        if(rolesToDelete.isEmpty()) {

            errorMessage =
                "Please select at least one role."

            activeAlert =
                ActiveAlert.ERROR

            return

        }



        val userRef =
            db.collection("users")
                .document(from.id)



        val remainingRoles =
            from.activeRoles.toMutableList()



        val missingRoles =
            mutableListOf<String>()



        rolesToDelete.forEach { role ->

            if(
                remainingRoles.contains(role)
            ) {

                remainingRoles.remove(role)

            }
            else {

                missingRoles.add(role)

            }

        }



        userRef.update(
            "activeRoles",
            remainingRoles
        )
            .addOnSuccessListener {


                unsubscribeFromTopics(
                    rolesToDelete
                )


                successMessage =
                    "Roles successfully removed."



                if(missingRoles.isNotEmpty()) {

                    successMessage +=
                        "\nWarning: User did not have: " +
                                missingRoles.joinToString(", ")

                }



                errorMessage = ""

                didSucceed = true

                activeAlert =
                    ActiveAlert.SUCCESS


            }
            .addOnFailureListener { error ->


                errorMessage =
                    error.message
                        ?: "Unable to remove roles."


                successMessage = ""

                activeAlert =
                    ActiveAlert.ERROR

            }


    }




    private fun unsubscribeFromTopics(
        roles: List<String>
    ) {


        roles.forEach { role ->


            FirebaseMessaging
                .getInstance()
                .unsubscribeFromTopic(role)

                .addOnSuccessListener {

                    println(
                        "Unsubscribed from $role"
                    )

                }

                .addOnFailureListener {

                    println(
                        "Failed to unsubscribe from $role"
                    )

                }

        }

    }




    fun clearAlert() {

        activeAlert = null

    }


    fun resetSuccess() {

        didSucceed = false

    }

}