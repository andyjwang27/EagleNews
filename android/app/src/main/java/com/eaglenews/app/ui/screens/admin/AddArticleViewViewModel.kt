package com.eaglenews.app.ui.screens.admin

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eaglenews.app.ui.screens.todo.ActiveAlert
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class AddArticleViewViewModel : ViewModel() {
    private val db =
        FirebaseFirestore.getInstance()
    private val storage =
        FirebaseStorage.getInstance()
    var activeAlert by mutableStateOf<ActiveAlert?>(null)
        private set
    var errorMessage by mutableStateOf("")
        private set
    var publisher by mutableStateOf("")

    fun fetchUser() {
        val uid =
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid
                ?: return

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                publisher =
                    document.getString("name")
                        ?: ""
            }
    }

    fun addArticle(
        title: String,
        summary: String,
        tags: List<String>,
        images: List<Uri>
    ) {
        if(
            title.trim().isEmpty()
            ||
            summary.trim().isEmpty()
            ||
            tags.isEmpty()
        ) {
            errorMessage =
                "Please fill in all fields."
            activeAlert =
                ActiveAlert.ERROR
            return
        }

        uploadImages(images) { urls ->
            saveArticle(
                title = title,
                summary = summary,
                tags = tags,
                imageURLs = urls
            )
        }
    }

    private fun saveArticle(
        title: String,
        summary: String,
        tags: List<String>,
        imageURLs: List<String>
    ) {
        val uid =
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid
                ?: return

        val article = hashMapOf(
            "title" to
                    title.trim(),
            "summary" to
                    summary.trim(),
            "publishedAt" to
                    Timestamp.now(),
            "tags" to tags,
            "imageURLs" to imageURLs,
            "publisher" to publisher,
            "publisherId" to uid,
            "pinned" to false
        )

        db.collection("articles")
            .add(article)
            .addOnSuccessListener {
                activeAlert =
                    ActiveAlert.SUCCESS
            }
            .addOnFailureListener {
                errorMessage =
                    it.message
                        ?: "Unable to save article."
                activeAlert =
                    ActiveAlert.ERROR
            }
    }

    private fun uploadImages(
        images: List<Uri>,
        completion: (List<String>) -> Unit
    ) {
        if(images.isEmpty()) {
            completion(
                emptyList()
            )
            return
        }

        val urls =
            mutableListOf<String>()

        var completed = 0

        images.forEach { uri ->
            uploadImage(uri) { url ->
                if(url != null) {
                    urls.add(url)
                }
                completed++

                if(completed == images.size) {
                    completion(urls)
                }
            }
        }
    }

    private fun uploadImage(
        uri: Uri,
        completion: (String?) -> Unit
    ) {
        val filename =
            UUID.randomUUID()
                .toString()

        val ref =
            storage.reference
                .child(
                    "articleImages/$filename.jpg"
                )

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { url ->
                        completion(
                            url.toString()
                        )
                    }
                    .addOnFailureListener {
                        completion(null)
                    }
            }
            .addOnFailureListener {
                completion(null)
            }
    }

    fun clearAlert() {
        activeAlert = null
    }
}