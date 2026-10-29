package com.eaglenews.app.ui.screens.links

import androidx.lifecycle.ViewModel
import com.eaglenews.app.models.Link
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LinksViewViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null
    private val _links = MutableStateFlow<List<Link>>(emptyList())
    val links: StateFlow<List<Link>> =
        _links.asStateFlow()

    fun startListeningForLinks(
        userId: String
    ) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val roles =
                    snapshot.get("activeRoles")
                            as? List<String>
                        ?: emptyList()
                listenForLinks(
                    roles
                )
            }
    }
    private fun listenForLinks(
        activeRoles: List<String>
    ) {
        listener?.remove()
        listener =
            db.collection("links")
                .addSnapshotListener { snapshot, error ->
                    if(error != null)
                        return@addSnapshotListener
                    val links =
                        snapshot?.documents
                            ?.mapNotNull { doc ->
                                val link =
                                    doc.toObject(Link::class.java)
                                link?.copy(
                                    id = doc.id
                                )
                            }
                            ?.filter {
                                it.tags
                                    .intersect(activeRoles.toSet())
                                    .isNotEmpty()
                            }
                            ?: emptyList()
                    _links.value = links
                }
    }

    fun deleteLink(
        link: Link
    ) {
        val id = link.id ?: return
        db.collection("links")
            .document(id)
            .delete()
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}