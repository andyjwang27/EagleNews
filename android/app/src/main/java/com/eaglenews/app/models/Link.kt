package com.eaglenews.app.models

import com.google.firebase.firestore.DocumentId


data class Link(
    @DocumentId
    val id: String? = null,

    val name: String = "",

    val link: String = "",

    val tags: List<String> = emptyList()
)