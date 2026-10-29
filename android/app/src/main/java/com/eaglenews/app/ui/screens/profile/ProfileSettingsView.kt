package com.eaglenews.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.ui.components.AnimatedSheet
import com.eaglenews.app.ui.navigation.DeleteState
import com.eaglenews.app.ui.theme.Arial
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.DarkRed
import com.eaglenews.app.ui.theme.DefaultGray
import com.eaglenews.app.ui.theme.Olivita
import com.eaglenews.app.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsView(
    onDismiss: () -> Unit,
    authViewModel: AuthViewModel,
    viewModel: ProfileSettingsViewViewModel = viewModel()
) {
    val characterLimit = 20
    val scope = rememberCoroutineScope()

    var visible by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var isSaveError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    LaunchedEffect(visible) {
        if (visible) {
            viewModel.name = ""
        }
    }

    fun closeSheet() {
        visible = false
        scope.launch {
            delay(350)
            onDismiss()
        }
    }

    AnimatedSheet(
        title = "Settings",
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

                Spacer(Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = viewModel.name,
                        onValueChange = {
                            viewModel.name = it.take(characterLimit)
                        },
                        label = { Text("Name") },

                        modifier = Modifier
                            .width(200.dp)
                            .height(70.dp),

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

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (viewModel.name.isBlank()) {
                                isSaveError = true
                                showSaveDialog = true
                            } else {
                                viewModel.save {
                                    isSaveError = false
                                    showSaveDialog = true
                                    viewModel.name = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkNavy),
                        modifier = Modifier
                            .width(100.dp)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Save",
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(30.dp))
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "${viewModel.name.length} / $characterLimit",
                    modifier = Modifier
                        .padding(start = 10.dp),
                    color = Color.Black
                )

                Spacer(Modifier.height(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            authViewModel.logout()
                            closeSheet()
                                  },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkNavy
                        ),
                        modifier = Modifier
                            .width(140.dp)
                            .padding(start = 10.dp)
                    ) {
                        Row {
                            Text(
                                text = "Logout",
                                fontFamily = Arial,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.PersonOff,
                                contentDescription = "Logout",
                                modifier = Modifier
                                    .size(20.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = { viewModel.startDeleteFlow() },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkNavy),
                        modifier = Modifier
                            .width(190.dp)
                            .padding(end = 10.dp)
                    ) {
                        Row {
                            Text(
                                text = "Delete Account",
                                fontFamily = Arial,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier
                                    .size(20.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
        if (viewModel.deleteState != DeleteState.IDLE) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clip(RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    when (viewModel.deleteState) {

                        DeleteState.CONFIRM_START -> {

                            Text(
                                text = "Delete Account?",
                                fontFamily = Olivita,
                                color = Color.White
                            )

                            Text(
                                text = "This action cannot be undone.",
                                fontFamily = Olivita,
                                color = Color.White
                            )

                            Button(
                                onClick = { viewModel.beginCountdown() },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkNavy),
                                modifier = Modifier.width(220.dp)
                            ) {
                                Text(
                                    text = "Start 5 second countdown",
                                    fontFamily = Arial,
                                    color = Color.White
                                )
                            }

                            Button(
                                onClick = { viewModel.cancelDelete() },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkRed),
                                modifier = Modifier.width(220.dp)
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontFamily = Arial,
                                    color = Color.White
                                )
                            }
                        }

                        DeleteState.COUNTING_DOWN -> {

                            Text(
                                text = "Deleting in ${viewModel.countdown}",
                                fontFamily = Olivita,
                                color = Color.White
                            )

                            Text(
                                text = "You can cancel anytime.",
                                fontFamily = Olivita,
                                color = Color.White
                            )

                            Button(
                                onClick = { viewModel.cancelDelete() },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkRed),
                                modifier = Modifier.width(220.dp)
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontFamily = Arial,
                                    color = Color.White
                                )
                            }
                        }

                        DeleteState.READY_TO_DELETE -> {

                            Text(
                                text = "Ready to delete account",
                                fontFamily = Olivita,
                                color = Color.White
                            )

                            Button(
                                onClick = {
                                    viewModel.deleteAccount()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkNavy),
                                modifier = Modifier.width(220.dp)
                            ) {
                                Text(
                                    text = "Delete Now",
                                    fontFamily = Arial,
                                    color = Color.White
                                )
                            }

                            Button(
                                onClick = { viewModel.cancelDelete() },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkRed),
                                modifier = Modifier.width(220.dp)
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontFamily = Arial,
                                    color = Color.White
                                )
                            }
                        }

                        DeleteState.IDLE -> Unit
                    }
                }
            }
        }
    }
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(if (isSaveError) "Error" else "Success")
            },
            text = {
                Text(
                    if (isSaveError)
                        "Please enter a name."
                    else
                        "Name updated successfully."
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkNavy
                    ),
                    onClick = {
                        showSaveDialog = false
                    }
                ) {
                    Text(
                        text = "OK",
                        color = Color.White
                    )
                }
            }
        )
    }
}

