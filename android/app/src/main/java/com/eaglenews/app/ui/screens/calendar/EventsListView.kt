package com.eaglenews.app.ui.screens.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.models.EventItem
import com.eaglenews.app.ui.components.AnimatedSheet
import com.eaglenews.app.ui.theme.DarkRed
import com.eaglenews.app.util.isBeforeToday
import com.eaglenews.app.util.startOfDay
import com.eaglenews.app.viewmodel.ContentViewModel

enum class EventListSheet { EDIT_EVENT, EVENT_VIEW }

@Composable
fun EventsListView(userId: String, onDismiss: () -> Unit) {
    val contentViewModel: ContentViewModel = viewModel()
    val viewModel: EventsListViewModel = viewModel(factory = EventsListViewModel.Factory(userId))
    val items by viewModel.items.collectAsState()

    var selectedEvent by remember { mutableStateOf<EventItem?>(null) }
    var eventToDelete by remember { mutableStateOf<EventItem?>(null) }
    var activeSheet by remember { mutableStateOf<EventListSheet?>(null) }

    val pastEvents = remember(items) {
        items.filter { isBeforeToday(startOfDay(it.date)) }
            .sortedByDescending { it.date }
    }
    val currentEvents = remember(items) {
        items.filter { !isBeforeToday(startOfDay(it.date)) }
            .sortedWith(compareByDescending<EventItem> { it.pinned }.thenBy { it.date })
    }

    AnimatedSheet(title = "Events", onDismiss = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            if (currentEvents.isNotEmpty()) {
                item {
                    Text(
                        text = "Events",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                itemsIndexed(
                    items = currentEvents,
                    key = { _, event -> event.id ?: event.title }
                ) { index, event ->
                    val showPin = !event.isGroup || contentViewModel.isAdmin || contentViewModel.isClubLeader
                    SwipeRevealRowEvent(
                        pinned = event.pinned,
                        showPin = showPin,
                        onEdit = {
                            selectedEvent = event
                            activeSheet = EventListSheet.EDIT_EVENT
                        },
                        onTogglePin = { viewModel.togglePin(event) },
                        onDelete = { if (!event.id.isNullOrEmpty()) eventToDelete = event }
                    ) {
                        EventItemRow(
                            item = event,
                            isFirst = index == 0,
                            isLast = index == currentEvents.lastIndex,
                            modifier = Modifier.clickable {
                                selectedEvent = event
                                activeSheet = EventListSheet.EVENT_VIEW
                            }
                        )
                    }
                }
            }

            if (pastEvents.isNotEmpty()) {
                item {
                    Text(
                        text = "Past",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                itemsIndexed(
                    items = pastEvents,
                    key = { _, event -> event.id ?: event.title }
                ) { index, event ->
                    SwipeRevealRowEvent(
                        pinned = event.pinned,
                        showPin = false,
                        onEdit = {
                            selectedEvent = event
                            activeSheet = EventListSheet.EDIT_EVENT
                        },
                        onTogglePin = {},
                        onDelete = { if (!event.id.isNullOrEmpty()) eventToDelete = event }
                    ) {
                        EventItemRow(
                            item = event,
                            contentAlpha = 0.4f,
                            isFirst = index == 0,
                            isLast = index == pastEvents.lastIndex,
                            modifier = Modifier.clickable {
                                selectedEvent = event
                                activeSheet = EventListSheet.EVENT_VIEW
                            }
                        )
                    }
                }
            }
        }
    }

    eventToDelete?.let { event ->
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            title = { Text("Delete Event?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkRed
                    ),
                    onClick = {
                        event.id?.let {
                            viewModel.delete(it)
                        }
                        eventToDelete = null
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        eventToDelete = null
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.White
                    )
                }
            }
        )
    }

    when (activeSheet) {
        EventListSheet.EDIT_EVENT -> selectedEvent?.let { event ->
            EditEventView(event = event, onDismiss = { activeSheet = null })
        }
        EventListSheet.EVENT_VIEW -> selectedEvent?.let { event ->
            EventView(selectedEvent = event, onDismiss = { activeSheet = null })
        }
        null -> {}
    }
}