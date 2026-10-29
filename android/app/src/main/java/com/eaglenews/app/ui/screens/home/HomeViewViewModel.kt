package com.eaglenews.app.ui.screens.home

import androidx.lifecycle.ViewModel
import com.eaglenews.app.models.Article
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    private var hasStarted = false

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles

    fun startListening(userId: String) {
        if (hasStarted) return
        hasStarted = true

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val roles = doc.get("activeRoles") as? List<String> ?: emptyList()
                listenForArticles(roles)
            }
    }

    private fun listenForArticles(activeRoles: List<String>) {

        listener?.remove()

        listener = db.collection("articles")
            .orderBy("pinned", Query.Direction.DESCENDING)
            .orderBy("publishedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener

                val list = snapshot.documents.mapNotNull { doc ->
                    val article = doc.toObject(Article::class.java)?.copy(id = doc.id)
                        ?: return@mapNotNull null

                    val match = article.tags.any { it in activeRoles }
                    if (match) article else null
                }

                _articles.value = list
            }
    }

    fun togglePin(article: Article) {
        val id = article.id ?: return

        db.collection("articles")
            .document(id)
            .update("pinned", !article.pinned)
    }

    fun deleteArticle(articleId: String) {
        db.collection("articles")
            .document(articleId)
            .delete()
    }

    override fun onCleared() {
        listener?.remove()
    }
}