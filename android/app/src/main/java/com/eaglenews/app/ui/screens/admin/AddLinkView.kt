package com.eaglenews.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.models.RoleItem
import com.eaglenews.app.ui.screens.selection.SelectionChipFlow
import com.eaglenews.app.ui.screens.selection.SelectionFilterChip
import com.eaglenews.app.ui.screens.todo.ActiveAlert
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.DefaultGray


@Composable
fun AddLinkView(
    selectedRoles: Set<RoleItem>,
    onSelectedRolesChange: (Set<RoleItem>) -> Unit,
    onShowRolePicker: () -> Unit
) {
    val viewModel: AddLinkViewViewModel = viewModel()
    var name by remember {
        mutableStateOf("")
    }
    var link by remember {
        mutableStateOf("")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 40.dp
                )
        ) {
            Spacer(
                Modifier.height(75.dp)
            )

            Text(
                text = "Create a Link",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text("Name")
                },
                modifier = Modifier
                    .fillMaxWidth(),
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
                Modifier.height(15.dp)
            )

            OutlinedTextField(
                value = link,
                onValueChange = {
                    link = it
                },
                label = {
                    Text("Link")
                },
                modifier = Modifier
                    .fillMaxWidth(),
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
                "Select Roles:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Button(
                onClick = onShowRolePicker,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkNavy
                ),
                modifier = Modifier
                    .width(150.dp)
                    .height(45.dp)
            ) {
                Text(
                    text = "Select Roles",
                    color = Color.White
                )
            }

            if (selectedRoles.isNotEmpty()) {
                Spacer(
                    Modifier.height(15.dp)
                )

                Text(
                    "Selected Roles:",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    Modifier.height(10.dp)
                )

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(25.dp)
                        )
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    SelectionChipFlow {
                        selectedRoles.forEach { role ->
                            SelectionFilterChip(
                                text = role.name.formattedRole(),
                                onRemove = {
                                    onSelectedRolesChange(
                                        selectedRoles - role
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(
                Modifier.height(30.dp)
            )

            Button(
                onClick = {
                    viewModel.addLink(
                        name = name,
                        link = link,
                        tags = selectedRoles.map {
                            it.name
                        },
                        onSuccess = {
                            name = ""
                            link = ""
                            onSelectedRolesChange(
                                emptySet()
                            )
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkNavy
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    "Publish",
                    color = Color.White
                )
            }
        }
        viewModel.activeAlert?.let { alert ->

            AlertDialog(
                onDismissRequest = {
                    viewModel.clearAlert()
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
                    Text(
                        if (alert == ActiveAlert.ERROR)
                            viewModel.errorMessage
                        else
                            "Link saved successfully."
                    )
                },

                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkNavy
                        ),
                        onClick = {
                            viewModel.clearAlert()
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