package com.eaglenews.app.ui.screens.calendar

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.eaglenews.app.models.EventItem
import com.eaglenews.app.ui.screens.todo.ActiveAlert
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class AddEventViewViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    var activeAlert by mutableStateOf<ActiveAlert?>(null)
    var errorMessage by mutableStateOf("")
    private var publisher = ""

    fun addEvent(
        title: String,
        selectedDate: Date,
        selectedTime: Date,
        onSuccess: () -> Unit
    ) {

        if(title.trim().isEmpty()) {

            errorMessage = "Please enter an event title."
            activeAlert = ActiveAlert.ERROR
            return

        }


        val uid = FirebaseAuth.getInstance()
            .currentUser
            ?.uid


        if(uid == null) {

            errorMessage = "Unable to fetch user."
            activeAlert = ActiveAlert.ERROR
            return

        }


        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->


                publisher =
                    document.getString("name") ?: ""


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


                val event = EventItem(
                    title = title,
                    date = Timestamp(calendar.time),
                    pinned = false,
                    publisher = publisher,
                    publisherId = uid,
                    tags = listOf("user"),
                    isGroup = false,
                    reminderSent = false,
                    recipientIds = emptyList()
                )


                db.collection("users")
                    .document(uid)
                    .collection("events")
                    .add(event)
                    .addOnSuccessListener {

                        activeAlert = ActiveAlert.SUCCESS

                    }
                    .addOnFailureListener {

                        errorMessage =
                            it.message ?: "Unable to save event."

                        activeAlert = ActiveAlert.ERROR

                    }

            }
            .addOnFailureListener {

                errorMessage =
                    "Unable to fetch user information."

                activeAlert = ActiveAlert.ERROR

            }
    }
}