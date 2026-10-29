package com.eaglenews.app.ui.screens.calendar

import androidx.lifecycle.ViewModel
import com.eaglenews.app.models.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date
import com.eaglenews.app.ui.screens.todo.ActiveAlert
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class AddGroupEventViewViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    var activeAlert by mutableStateOf<ActiveAlert?>(null)
        private set

    var errorMessage by mutableStateOf("")
        private set

    var publisher = ""

    fun assignEvent(
        title: String,
        selectedDate: Date,
        selectedTime: Date,
        selectedUsers: Set<User>,
        tags: List<String>,
        onSuccess: () -> Unit
    ) {


        if(title.trim().isEmpty()) {

            errorMessage = "Please enter a title."
            activeAlert = ActiveAlert.ERROR
            return

        }


        if(selectedUsers.isEmpty() && tags.isEmpty()) {

            errorMessage = "Select at least one user or role."
            activeAlert = ActiveAlert.ERROR

            return
        }



        val uid =
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid


        if(uid == null) {

            errorMessage = "Unable to fetch user"
            activeAlert = ActiveAlert.ERROR

            return
        }



        // combine date + time

        val calendar = Calendar.getInstance()

        calendar.time = selectedDate


        val timeCalendar = Calendar.getInstance()

        timeCalendar.time = selectedTime


        calendar.set(
            Calendar.HOUR_OF_DAY,
            timeCalendar.get(Calendar.HOUR_OF_DAY)
        )


        calendar.set(
            Calendar.MINUTE,
            timeCalendar.get(Calendar.MINUTE)
        )


        calendar.set(
            Calendar.SECOND,
            0
        )


        val dateTime = calendar.time



        fetchPublisher(uid) { publisherName ->


            var targetUserIds =
                selectedUsers
                    .mapNotNull { it.id }
                    .toMutableSet()



            fun createEvents() {


                val event = hashMapOf(

                    "title" to title,

                    "date" to Timestamp(dateTime),

                    "pinned" to false,

                    "publisher" to publisherName,

                    "publisherId" to uid,

                    "tags" to tags,

                    "isGroup" to true,

                    "reminderSent" to false,

                    "recipientIds" to targetUserIds.toList()

                )



                val batch =
                    db.batch()



                targetUserIds.forEach { userId ->


                    val ref =
                        db.collection("users")
                            .document(userId)
                            .collection("events")
                            .document()



                    batch.set(
                        ref,
                        event
                    )

                }



                batch.commit()
                    .addOnSuccessListener {


                        db.collection("groupNotifications")
                            .add(
                                hashMapOf(

                                    "title" to title,

                                    "publisher" to publisherName,

                                    "publisherId" to uid,

                                    "recipientIds" to targetUserIds.toList()

                                )
                            )


                        activeAlert = ActiveAlert.SUCCESS

                    }

                    .addOnFailureListener {


                        errorMessage = "Unown error"
                        activeAlert = ActiveAlert.ERROR

                    }

            }



            if(tags.isEmpty()) {

                createEvents()

                return@fetchPublisher

            }



            // Add users matching roles

            db.collection("users")
                .whereArrayContainsAny(
                    "activeRoles",
                    tags
                )
                .get()
                .addOnSuccessListener { snapshot ->


                    snapshot.documents.forEach { doc ->

                        targetUserIds.add(
                            doc.id
                        )

                    }


                    createEvents()

                }

                .addOnFailureListener {


                    errorMessage = it.message ?: "Unable to fetch users"
                    activeAlert = ActiveAlert.ERROR

                }

        }


    }



    private fun fetchPublisher(
        uid: String,
        completion: (String) -> Unit
    ) {


        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->


                completion(
                    document.getString("name")
                        ?: ""
                )

            }

            .addOnFailureListener {


                completion("")

            }

    }
    fun clearAlert(){
        activeAlert = null
    }

}