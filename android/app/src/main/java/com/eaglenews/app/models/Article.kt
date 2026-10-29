package com.eaglenews.app.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.QueryDocumentSnapshot

data class Article(
    @DocumentId val id: String? = null,
    val title: String = "",
    val summary: String = "",
    val publishedAt: Timestamp? = null,
    val tags: List<String> = emptyList(),
    val imageURLs: List<String> = emptyList(),
    val publisher: String = "",
    val publisherId: String = "",
    val pinned: Boolean = false
) {
    companion object
}

fun Article.Companion.from(doc: QueryDocumentSnapshot): Article? {
    return try {
        val title = doc.getString("title") ?: return null
        val summary = doc.getString("summary") ?: return null
        val publisher = doc.getString("publisher") ?: return null
        val publisherId = doc.getString("publisherId") ?: return null
        val timestamp = doc.getTimestamp("publishedAt") ?: return null
        val pinned = doc.getBoolean("pinned") ?: false

        Article(
            id = doc.id,
            title = title,
            summary = summary,
            publishedAt = timestamp,
            tags = doc.get("tags") as? List<String> ?: emptyList(),
            imageURLs = doc.get("imageURLs") as? List<String> ?: emptyList(),
            publisher = publisher,
            publisherId = publisherId,
            pinned = pinned
        )
    } catch (e: Exception) {
        null
    }
}