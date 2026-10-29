package com.eaglenews.app.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val joined: Long = 0,
    val activeRoles: List<String> = emptyList(),
    val eventMinutesBefore: Int = 5
)