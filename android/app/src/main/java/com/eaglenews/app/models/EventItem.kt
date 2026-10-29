package com.eaglenews.app.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.Exclude

data class EventItem(
    @get:Exclude
    val id: String? = null,
    val title: String = "",
    val date: Timestamp? = null,
    val pinned: Boolean = false,
    val publisher: String = "",
    val publisherId: String = "",
    val tags: List<String> = emptyList(),

    @get:PropertyName("isGroup")
    val isGroup: Boolean = false,

    val reminderSent: Boolean = false,
    val recipientIds: List<String> = emptyList()
)