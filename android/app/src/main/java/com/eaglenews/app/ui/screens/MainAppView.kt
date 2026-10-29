package com.eaglenews.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.viewmodel.AuthViewModel
import com.eaglenews.app.ui.components.SideMenu
import com.eaglenews.app.ui.navigation.AppState
import com.eaglenews.app.ui.navigation.TabIcon
import com.eaglenews.app.ui.screens.admin.AdminScreen
import com.eaglenews.app.ui.screens.calendar.CalendarView
import com.eaglenews.app.ui.screens.home.HomeView
import com.eaglenews.app.ui.screens.links.LinksView
import com.eaglenews.app.ui.screens.profile.ProfileView
import com.eaglenews.app.ui.screens.todo.ToDoView
import com.eaglenews.app.viewmodel.ContentViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainAppView(appState: AppState) {
    val context = LocalContext.current

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                println("Notifications enabled")
            } else {
                println("Notifications denied")
            }

        }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )

            }
        }
    }
    val authViewModel: AuthViewModel = viewModel()
    val contentViewModel: ContentViewModel = viewModel()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    Box(modifier = Modifier.fillMaxSize()) {

        when (appState.selectedTab) {

            TabIcon.HomeView -> {
                if (userId != null) {
                    HomeView(
                        appState, userId = userId,
                        contentViewModel = contentViewModel
                    )
                }
            }
            TabIcon.TodoView -> {
                if (userId != null) {
                    ToDoView(appState, userId = userId)
                }
            }
            TabIcon.CalendarView -> CalendarView(appState)
            TabIcon.LinksView -> {
                if (userId != null) {
                    LinksView(
                        appState = appState,
                        userId = userId,
                        contentViewModel = contentViewModel
                    )
                }
            }
            TabIcon.ProfileView -> {
                if (userId != null) {
                    ProfileView(
                        appState = appState,
                        authViewModel = authViewModel
                    )
                }
            }
            TabIcon.AdminView -> AdminScreen(appState)
        }

        if (appState.showMenu) {
            SideMenu(
                selectedItem = appState.selectedTab,
                onItemSelected = {
                    appState.selectedTab = it   // switch screen
                },
                showMenu = appState.showMenu,
                onClose = { appState.showMenu = false },
                isAdmin = true
            )
        }
    }
}