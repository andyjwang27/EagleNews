package com.eaglenews.app.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eaglenews.app.models.EventItem
import com.eaglenews.app.ui.components.AnimatedSheet
import com.eaglenews.app.util.dateFormatter
import com.eaglenews.app.util.timeFormatter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun EventView(selectedEvent: EventItem, onDismiss: () -> Unit) {
    AnimatedSheet(title = selectedEvent.title, onDismiss = onDismiss) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                val eventDate = selectedEvent.date
                Text(
                    text = "Event: ${selectedEvent.title}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = eventDate?.let {
                        "Published by ${selectedEvent.publisher} | " +
                                "${dateFormatter.format(it.toDate())} at ${timeFormatter.format(it.toDate())}"
                    } ?: "Published by ${selectedEvent.publisher}"
                )
            }
        }
    }
}