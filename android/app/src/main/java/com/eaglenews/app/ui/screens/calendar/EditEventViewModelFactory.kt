package com.eaglenews.app.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eaglenews.app.models.EventItem


class EditEventViewModelFactory(
    private val event: EventItem
) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if(modelClass.isAssignableFrom(EditEventViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return EditEventViewModel(event) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel"
        )
    }
}