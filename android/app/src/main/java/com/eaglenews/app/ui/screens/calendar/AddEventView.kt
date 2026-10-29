package com.eaglenews.app.ui.screens.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.ui.components.AnimatedSheet
import com.eaglenews.app.ui.screens.todo.ActiveAlert
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.DarkRed
import com.eaglenews.app.ui.theme.DefaultGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventView(
    onDismiss: () -> Unit
) {
    val viewModel: AddEventViewViewModel = viewModel()
    var selectedDate by remember {
        mutableStateOf(Date())
    }
    var showTimePicker by remember {
        mutableStateOf(false)
    }

    var selectedTime by remember {
        mutableStateOf(Date())
    }
    var title by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    var visible by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        visible = true
    }
    fun closeSheet() {
        visible = false
        scope.launch {
            delay(350)
            onDismiss()
        }
    }
    AnimatedSheet(
        title = "Add Event",
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
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 20.dp,
                        bottom = 30.dp
                    )
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
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
                    )
                )
                Spacer(
                    Modifier.height(20.dp)
                )
                Text(
                    "Date",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(
                    Modifier.height(10.dp)
                )
                SelectedDateCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = {
                        selectedDate = it
                    }
                )
                Spacer(
                    Modifier.height(20.dp)
                )
                Text(
                    "Time",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(
                    Modifier.height(10.dp)
                )
                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(
                        1.dp,
                        DefaultGray
                    ),
                    onClick = {
                        showTimePicker = true
                    }
                ) {
                    val cal = Calendar.getInstance()
                    cal.time = selectedTime

                    val hour = cal.get(Calendar.HOUR)
                    val minute = cal.get(Calendar.MINUTE)
                    val amPm = if (
                        cal.get(Calendar.AM_PM) == Calendar.AM
                    ) "AM" else "PM"

                    Text(
                        text = String.format(
                            "%02d:%02d %s",
                            if (hour == 0) 12 else hour,
                            minute,
                            amPm
                        )
                    )
                }
                Spacer(
                    Modifier.height(30.dp)
                )
                Button(
                    onClick = {
                        viewModel.addEvent(
                            title = title,
                            selectedDate = selectedDate,
                            selectedTime = selectedTime,
                            onSuccess = {
                                closeSheet()
                            }
                        )
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
            }
            viewModel.activeAlert?.let { alert ->
                when (alert) {
                    ActiveAlert.ERROR -> {
                        AlertDialog(
                            onDismissRequest = {
                                viewModel.activeAlert = null
                            },
                            title = {
                                Text("Error")
                            },
                            text = {
                                Text(viewModel.errorMessage)
                            },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DarkNavy
                                    ),
                                    onClick = {
                                        viewModel.activeAlert = null
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

                    ActiveAlert.SUCCESS -> {
                        AlertDialog(
                            onDismissRequest = {},
                            title = {
                                Text("Success")
                            },
                            text = {
                                Text("Event added successfully.")
                            },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DarkNavy
                                    ),
                                    onClick = {
                                        viewModel.activeAlert = null
                                        closeSheet()
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
            }
            if (showTimePicker) {
                val calendar = Calendar.getInstance()
                calendar.time = selectedTime

                val timePickerState = rememberTimePickerState(
                    initialHour = calendar.get(Calendar.HOUR_OF_DAY),
                    initialMinute = calendar.get(Calendar.MINUTE),
                    is24Hour = false
                )

                Dialog(
                    onDismissRequest = {
                        showTimePicker = false
                    }
                ) {

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            TimePicker(
                                state = timePickerState
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {

                                TextButton(
                                    onClick = {
                                        showTimePicker = false
                                    }
                                ) {
                                    Text(
                                        text = "Cancel",
                                        color = DarkRed
                                    )
                                }

                                TextButton(
                                    onClick = {

                                        val newTime =
                                            Calendar.getInstance()

                                        newTime.set(
                                            Calendar.HOUR_OF_DAY,
                                            timePickerState.hour
                                        )

                                        newTime.set(
                                            Calendar.MINUTE,
                                            timePickerState.minute
                                        )

                                        selectedTime =
                                            newTime.time

                                        showTimePicker = false
                                    }
                                ) {
                                    Text(
                                        text = "Done",
                                        color = DarkNavy
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}