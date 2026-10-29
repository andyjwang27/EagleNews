package com.eaglenews.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.eaglenews.app.ui.screens.RootScreen
import com.eaglenews.app.ui.theme.EagleNewsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EagleNewsTheme {
                RootScreen()
            }
        }
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                // Notifications enabled
            } else {
                // User denied notifications
            }
        }
}