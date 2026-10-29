package com.eaglenews.app.ui.screens.register

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.eaglenews.app.ui.components.AnimatedSheet
import com.eaglenews.app.ui.screens.todo.ActiveAlert
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.DefaultGray
import com.eaglenews.app.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(
    onDismiss: () -> Unit,
    onGoogleLogin: () -> Unit,
    authViewModel: AuthViewModel,
    onRegistrationSuccess: () -> Unit
) {
    var name by rememberSaveable {
        mutableStateOf("")
    }

    var email by rememberSaveable {
        mutableStateOf("")
    }
    AnimatedSheet(
        title = "Register",
        onDismiss = onDismiss
    ) {
        val focusManager = LocalFocusManager.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        focusManager.clearFocus()
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Spacer(Modifier.height(15.dp))

                Text(
                    text = "Register Using Your Apps Account",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(15.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        if (it.length <= 20) {
                            name = it
                        }
                    },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,

                        focusedIndicatorColor = DefaultGray,
                        unfocusedIndicatorColor = DefaultGray,

                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,

                        cursorColor = Color.Black,
                        errorCursorColor = Color.Black,

                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                Text(
                    "${name.length} / 20",
                    modifier = Modifier.padding(
                        top = 4.dp,
                        start = 15.dp
                    )
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    label = {
                        Text("Apps Address")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,

                        focusedIndicatorColor = DefaultGray,
                        unfocusedIndicatorColor = DefaultGray,

                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,

                        cursorColor = Color.Black,
                        errorCursorColor = Color.Black,

                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                Spacer(Modifier.height(20.dp))

                if (
                    name.isNotBlank() &&
                    email.isNotBlank() &&
                    (
                            email.contains("@apps.stratford.org") ||
                                    email.contains("rowdieeagle@gmail.com")
                            )
                ) {
                    Text(
                        "Sign In Using Google:",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(10.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkNavy
                        ),
                        onClick = {
                            Log.d("REGISTER", "Register has begun")
                            authViewModel.beginRegistration(
                                name = name,
                                email = email
                            )
                            onGoogleLogin()
                        }
                    ) {
                        Text(
                            "Continue with Google",
                            color = Color.White
                        )
                    }
                }
            }

            authViewModel.activeAlert?.let { alert ->
                AlertDialog(
                    onDismissRequest = {
                        authViewModel.clearAlert()
                    },
                    title = {
                        Text(
                            if (alert == ActiveAlert.ERROR)
                                "Error"
                            else
                                "Success"
                        )
                    },
                    text = {
                        Text(authViewModel.alertMessage)
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (alert == ActiveAlert.SUCCESS) {
                                    onRegistrationSuccess()
                                    authViewModel.finishRegistration()
                                    onDismiss()
                                }

                                authViewModel.clearAlert()
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}