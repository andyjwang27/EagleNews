package com.eaglenews.app.viewmodel

import androidx.lifecycle.ViewModel
import com.eaglenews.app.models.RoleItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class RolesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()


    private val _roles =
        MutableStateFlow<List<RoleItem>>(emptyList())


    val roles: StateFlow<List<RoleItem>> =
        _roles.asStateFlow()



    init {
        listenToRoles()
    }



    private fun listenToRoles() {

        db.collection("roles")
            .addSnapshotListener { snapshot, error ->


                if(error != null) {
                    return@addSnapshotListener
                }


                val roleList =
                    snapshot?.documents?.mapNotNull { document ->


                        document.toObject(RoleItem::class.java)
                            ?.copy(
                                id = document.id
                            )

                    } ?: emptyList()



                _roles.value = roleList
            }
    }
}