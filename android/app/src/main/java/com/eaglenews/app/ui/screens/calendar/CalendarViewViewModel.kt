package com.eaglenews.app.ui.screens.calendar

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.util.Date
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

enum class ActiveSheet {
    ADD_EVENT,
    EVENTS_LIST,
    SETTINGS,
    ADD_GROUP_EVENT
}

class CalendarViewModel : ViewModel() {

    var selectedDate by mutableStateOf(Date())
        private set

    var activeSheet by mutableStateOf<ActiveSheet?>(null)

    fun updateSelectedDate(date: Date) {
        selectedDate = date
    }
}