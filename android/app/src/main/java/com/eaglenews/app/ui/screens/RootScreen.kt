package com.eaglenews.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.ui.navigation.AppState
import com.eaglenews.app.ui.screens.login.LoginView
import com.eaglenews.app.ui.screens.todo.ActiveAlert
import com.eaglenews.app.viewmodel.AuthViewModel

@Composable
fun RootScreen(
    authViewModel: AuthViewModel = viewModel()
) {
    val user by authViewModel.user.collectAsState()
    val context = LocalContext.current

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { }

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

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        Log.d(
            "GOOGLE",
            "Result code = ${result.resultCode}"
        )

        Log.d(
            "GOOGLE",
            "Data = ${result.data}"
        )

        authViewModel.handleGoogleResult(result.data)
    }

    val appState = remember { AppState() }

    LaunchedEffect(authViewModel.registrationComplete) {

        if (authViewModel.registrationComplete) {
            requestNotificationPermission()
        }

    }

    if (user != null) {
        MainAppView(appState)
    } else {
        LoginView(
            onGoogleLogin = {
                Log.d("GOOGLE", "Launching Google Sign In")

                val intent = authViewModel.getGoogleSignInIntent()

                Log.d("GOOGLE", "Intent: $intent")

                launcher.launch(intent)
            },
            onRegistrationSuccess = {
                //requestNotificationPermission()
                authViewModel.finishRegistration()
            }
        )
    }

    authViewModel.activeAlert?.let { alert ->

        AlertDialog(
            onDismissRequest = {
                authViewModel.clearAlert()
            },
            title = {
                Text(
                    if(alert == ActiveAlert.SUCCESS)
                        "Success"
                    else
                        "Error"
                )
            },
            text = {
                Text(authViewModel.alertMessage)
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.clearAlert()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}