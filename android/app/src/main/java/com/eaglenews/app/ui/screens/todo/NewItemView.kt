package com.eaglenews.app.ui.screens.todo

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.ui.components.DefaultView
import com.eaglenews.app.ui.components.RoundButton
import com.eaglenews.app.ui.theme.Arial
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.DarkRed
import com.eaglenews.app.ui.theme.DefaultGray
import kotlinx.coroutines.launch

@Composable
fun NewItemView(onDismiss: () -> Unit) {
    val viewModel: NewItemViewModel = viewModel()

    val scope = rememberCoroutineScope()
    var visible by remember { mutableStateOf(false) }
    var offsetY by remember { mutableStateOf(0f) }

    val animatedOffsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 1000.dp,
        animationSpec = tween(350),
        label = "sheet_slide"
    )

    LaunchedEffect(Unit) {
        visible = true
    }

    fun closeSheet() {
        visible = false
        scope.launch {
            kotlinx.coroutines.delay(350)
            onDismiss()
        }
    }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            // Blurred background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { closeSheet() }
            )

            // Sheet itself
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.90f)
                        .offset(
                            y = animatedOffsetY + offsetY.dp
                        )
                        .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
                        .background(Color.White)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragEnd = {
                                    if (offsetY > 200f) closeSheet()
                                    offsetY = 0f
                                },
                                onVerticalDrag = { _, dragAmount ->
                                    offsetY += dragAmount
                                }
                            )
                        }
                ) {
                    DefaultView(
                        title = "New Item",
                        showMenu = false,
                        showMenuButton = false,
                        onMenuClick = {},
                        trailing = {
                            RoundButton(
                                icon = Icons.Default.Close,
                                background = DarkNavy,
                                onClick = { closeSheet() }
                            )
                        }
                    ) {
                        Column {
                            OutlinedTextField(
                                value = viewModel.title,
                                onValueChange = {
                                    viewModel.title = it
                                },
                                label = { Text("Title") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
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

                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
                                DateTimePickerField(
                                    label = "Due Date",
                                    date = viewModel.dueDate,
                                    onDateChange = { viewModel.dueDate = it }
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.save { success ->
                                        viewModel.activeAlert =
                                            if (success) ActiveAlert.SUCCESS else ActiveAlert.ERROR
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkNavy),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 20.dp),
                            ) {
                                Text(
                                    text = "Save",
                                    fontFamily = Arial,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        viewModel.activeAlert?.let { alert ->
                            val isError = alert == ActiveAlert.ERROR
                            AlertDialog(
                                onDismissRequest = {
                                    viewModel.activeAlert = null
                                },
                                title = { Text(if (isError) "Error" else "Success") },
                                text = { Text(if (isError) viewModel.errorMessage else "Item saved successfully.") },
                                confirmButton = {
                                    Button(
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isError) DarkRed else DarkNavy
                                        ),
                                        onClick = {
                                            viewModel.activeAlert = null
                                            if (!isError) onDismiss()
                                        }
                                    )
                                    {
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
            }
        }
    }
}