package com.eaglenews.app.ui.screens.todo

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.models.ToDoListItem
import com.eaglenews.app.ui.components.AnimatedSheet
import com.eaglenews.app.ui.components.RoundButton
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.DefaultGray

@Composable
fun EditToDoView(
    selectedToDo: ToDoListItem,
    onDismiss: () -> Unit
) {
    val viewModel: EditToDoViewModel = viewModel(
        factory = EditToDoViewModel.Factory(selectedToDo)
    )

    val focusManager = LocalFocusManager.current
    AnimatedSheet(
        title = "Edit To-Do",
        onDismiss = onDismiss,

        trailing = { close ->

            RoundButton(
                icon = Icons.Default.Close,
                background = DarkNavy,
                onClick = close
            )
        }

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
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Edit To-Do",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = viewModel.title,
                    onValueChange = { viewModel.title = it },
                    label = { Text("Title") },
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

                Spacer(modifier = Modifier.height(16.dp))

                DateTimePickerField(
                    label = "Due Date",
                    date = viewModel.dueDate,
                    onDateChange = { viewModel.dueDate = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()

                        viewModel.update { success ->
                            viewModel.activeAlert =
                                if (success) ActiveAlert.SUCCESS
                                else ActiveAlert.ERROR
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkNavy
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Save",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))


                // Keep this INSIDE the composable scope
                viewModel.activeAlert?.let { alert ->

                    val isError = alert == ActiveAlert.ERROR

                    AlertDialog(
                        onDismissRequest = {
                            viewModel.activeAlert = null
                        },
                        title = {
                            Text(
                                if (isError) "Error"
                                else "Success"
                            )
                        },
                        text = {
                            Text(
                                if (isError)
                                    viewModel.errorMessage
                                else
                                    "Changes saved successfully."
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.activeAlert = null

                                    if (!isError) {
                                        onDismiss()
                                    }
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
}