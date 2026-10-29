package com.eaglenews.app.models

import com.google.firebase.firestore.DocumentSnapshot

data class ToDoListItem(
    val id: String = "",
    val title: String = "",
    val dueDate: Double = 0.0,
    val createdDate: Double = 0.0,
    val reminderSent: Boolean = false,
    var isDone: Boolean = false,
    var pinned: Boolean = false
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "dueDate" to dueDate,
        "createdDate" to createdDate,
        "reminderSent" to reminderSent,
        "isDone" to isDone,
        "pinned" to pinned
    )

    companion object {
        fun fromDocument(doc: DocumentSnapshot): ToDoListItem? {
            val data = doc.data ?: return null
            return ToDoListItem(
                id = doc.id,
                title = data["title"] as? String ?: "",
                dueDate = (data["dueDate"] as? Number)?.toDouble() ?: 0.0,
                createdDate = (data["createdDate"] as? Number)?.toDouble() ?: 0.0,
                reminderSent = data["reminderSent"] as? Boolean ?: false,
                isDone = data["isDone"] as? Boolean ?: false,
                pinned = data["pinned"] as? Boolean ?: false
            )
        }
    }
}

fun List<ToDoListItem>.sortPinnedAndDueDate(): List<ToDoListItem> =
    sortedWith(compareByDescending<ToDoListItem> { it.pinned }.thenBy { it.dueDate })
