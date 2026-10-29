package com.eaglenews.app.ui.screens.calendar

import androidx.compose.runtime.Composable
import com.eaglenews.app.ui.components.DefaultView
import com.eaglenews.app.ui.components.SideMenu
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.ui.navigation.AppState
import com.eaglenews.app.viewmodel.ContentViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CalendarView(appState: AppState) {
    val contentViewModel: ContentViewModel = viewModel()
    var showAddEvent by remember {
        mutableStateOf(false)
    }
    var showEventsList by remember {
        mutableStateOf(false)
    }
    var showAddGroupEvent by remember {
        mutableStateOf(false)
    }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    DefaultView(
        title = "Calendar",
        showMenu = appState.showMenu,
        showMenuButton = true,
        onMenuClick = {
            appState.showMenu = !appState.showMenu
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                CalendarButton(
                    title = "Add Event",
                    icon = Icons.Default.Add,
                    modifier = Modifier.weight(1f)
                ) {
                    showAddEvent = true
                }

                if (contentViewModel.isAdmin || contentViewModel.isClubLeader) {

                    CalendarButton(
                        title = "Group Events",
                        icon = Icons.Default.PersonAdd,
                        modifier = Modifier.weight(1f)
                    ) {
                        showAddGroupEvent = true
                    }
                }

                CalendarButton(
                    title = "Events",
                    icon = Icons.Default.List,
                    modifier = Modifier.weight(1f)
                ) {
                    showEventsList = true
                }
            }

            Spacer(Modifier.height(24.dp))

            GraphicalCalendar()
        }
    }

    if(showAddEvent) {
        AddEventView(
            onDismiss = {
                showAddEvent = false
            }
        )
    }

    if(showAddGroupEvent) {
        AddGroupEventView(
            onDismiss = {
                showAddGroupEvent = false
            }
        )
    }

    if(showEventsList) {
        if (userId != null) {
            EventsListView(
                userId = userId,
                onDismiss = {
                    showEventsList = false
                }
            )
        }
    }

    if (appState.showMenu) {
        SideMenu(
            selectedItem = appState.selectedTab,
            onItemSelected = { appState.selectedTab = it },
            showMenu = appState.showMenu,
            onClose = { appState.showMenu = false },
            isAdmin = true
        )
    }
}