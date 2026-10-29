package com.eaglenews.app.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class Screen {
    HOME,
    TODO,
    CALENDAR,
    LINKS,
    PROFILE,
    ADMIN
}

class AppState {
    var currentScreen by mutableStateOf(Screen.HOME)
    var showMenu by mutableStateOf(false)
    var selectedTab by mutableStateOf(TabIcon.HomeView)
}