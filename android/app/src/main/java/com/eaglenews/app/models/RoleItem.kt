package com.eaglenews.app.models

import com.google.firebase.firestore.DocumentId

data class RoleItem(
    @DocumentId
    val id: String? = null,

    val name: String = "",

    val priorityLevel: String = "",

    val type: String = ""
)