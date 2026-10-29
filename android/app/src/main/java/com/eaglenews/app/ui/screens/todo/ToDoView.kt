package com.eaglenews.app.ui.screens.todo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.eaglenews.app.ui.navigation.AppState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ToDoView(
    appState: AppState,
    userId: String
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val viewModel: ToDoListViewModel = viewModel(factory = ToDoListViewModel.Factory(userId))

    Box(modifier = Modifier.fillMaxSize()) {

        // Main content
        when (selectedTab) {
            0 -> ToDoTodayView(
                viewModel,
                appState.showMenu,
                onMenuClick = { appState.showMenu = !appState.showMenu }
            )

            1 -> ToDoWeekView(
                viewModel,
                appState.showMenu,
                onMenuClick = { appState.showMenu = !appState.showMenu }
            )

            2 -> ToDoAllView(
                viewModel,
                appState.showMenu,
                onMenuClick = { appState.showMenu = !appState.showMenu }
            )
        }

        // Floating tab bar
        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                icon = { Icon(Icons.Filled.CalendarToday, null) },
                label = { Text("Due Today") }
            )

            NavigationBarItem(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                icon = { Icon(Icons.Filled.DateRange, null) },
                label = { Text("Due This Week") }
            )

            NavigationBarItem(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                icon = { Icon(Icons.Filled.CalendarMonth, null) },
                label = { Text("All Due") }
            )
        }
    }
}