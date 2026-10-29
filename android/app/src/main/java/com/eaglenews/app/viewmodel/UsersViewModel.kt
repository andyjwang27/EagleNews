package com.eaglenews.app.viewmodel

import androidx.lifecycle.ViewModel
import com.eaglenews.app.models.User
import com.eaglenews.app.models.RoleItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class UsersViewModel : ViewModel() {


    private val db = FirebaseFirestore.getInstance()



    private val _users =
        MutableStateFlow<List<User>>(emptyList())


    val users: StateFlow<List<User>> =
        _users.asStateFlow()



    private val _roles =
        MutableStateFlow<List<RoleItem>>(emptyList())


    val roles: StateFlow<List<RoleItem>> =
        _roles.asStateFlow()



    init {

        listenToUsers()

        listenToRoles()

    }



    private fun listenToUsers() {


        db.collection("users")
            .addSnapshotListener { snapshot, error ->


                if(error != null) {
                    return@addSnapshotListener
                }


                val userList =
                    snapshot?.documents?.mapNotNull { document ->


                        document.toObject(User::class.java)
                            ?.copy(
                                id = document.id
                            )

                    } ?: emptyList()



                _users.value = userList
            }
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



    override fun onCleared() {

        super.onCleared()

        // Firestore listeners automatically clean up
        // when using ViewModel scope
    }
}