package com.eaglenews.app.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eaglenews.app.ui.theme.DarkNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphicalCalendar() {
    val datePickerState = rememberDatePickerState()
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            title = null,
            headline = null,
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                titleContentColor = Color.Black,
                headlineContentColor = Color.Black,
                weekdayContentColor = Color.Black,
                subheadContentColor = Color.Black,
                yearContentColor = Color.Black,
                currentYearContentColor = DarkNavy,
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = DarkNavy,
                dayContentColor = Color.Black,
                disabledDayContentColor = Color.Black,
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = DarkNavy
            ),
            modifier = Modifier
                .background(Color.White)
        )
    }
}