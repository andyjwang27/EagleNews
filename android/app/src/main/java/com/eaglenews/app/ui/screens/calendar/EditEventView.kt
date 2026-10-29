package com.eaglenews.app.ui.screens.calendar

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.models.EventItem
import com.eaglenews.app.ui.components.AnimatedSheet
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.DefaultGray

@Composable
fun EditEventView(
    event: EventItem,
    onDismiss: () -> Unit
) {

    val viewModel: EditEventViewModel = viewModel(
        factory = EditEventViewModelFactory(event)
    )


    val title by viewModel.title.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val alert by viewModel.activeAlert.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()


    AnimatedSheet(
        title = "Edit Event",
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
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {


                Text(
                    text = "Editing Event",
                    style = MaterialTheme.typography.titleMedium
                )


                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        viewModel.updateTitle(it)
                    },
                    label = {
                        Text("Event Title")
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
                    ),
                    singleLine = false
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                SelectedDateCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = {
                        viewModel.updateDate(it)
                    }
                )


                Spacer(
                    modifier = Modifier.weight(1f)
                )


                Button(
                    onClick = {
                        viewModel.updateEvent()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkNavy,
                        contentColor = Color.White,
                    )
                ) {
                    Text("Save")
                }

            }
        }

        when(alert) {
            EditAlert.ERROR -> {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.clearAlert()
                    },
                    title = {
                        Text("Error")
                    },
                    text = {
                        Text(errorMessage)
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.clearAlert()
                            }
                        ) {
                            Text("OK")
                        }

                    }
                )

            }
            EditAlert.SUCCESS -> {
                AlertDialog(
                    onDismissRequest = {},
                    title = {
                        Text("Success")
                    },
                    text = {
                        Text("Changes saved successfully.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.clearAlert()
                                onDismiss()
                            }
                        ) {
                            Text("OK")
                        }

                    }
                )

            }
            null -> Unit
        }
    }
}