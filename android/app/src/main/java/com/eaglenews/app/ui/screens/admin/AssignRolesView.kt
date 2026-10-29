package com.eaglenews.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.models.RoleItem
import com.eaglenews.app.models.User
import com.eaglenews.app.ui.screens.selection.SelectionChipFlow
import com.eaglenews.app.ui.screens.selection.SelectionFilterChip
import com.eaglenews.app.ui.screens.todo.ActiveAlert
import com.eaglenews.app.ui.theme.DarkNavy


@Composable
fun AssignRolesView(
    selectedUsers: Set<User>,
    selectedRoles: Set<RoleItem>,
    onSelectedUsersChange: (Set<User>) -> Unit,
    onSelectedRolesChange: (Set<RoleItem>) -> Unit,
    onShowUsersPicker: () -> Unit,
    onShowRolePicker: () -> Unit
) {
    val viewModel: AssignRolesViewViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            )
    ) {
        Spacer(
            modifier = Modifier.height(75.dp)
        )
        Text(
            text = "Assign Users Roles",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )


        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(25.dp),
            color = Color.White
        ) {

            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {


                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = onShowUsersPicker,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkNavy
                        ),
                        modifier = Modifier
                            .width(160.dp)
                            .height(45.dp)
                    ) {
                        Text(
                            text = "Select Users",
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = onShowRolePicker,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkNavy
                        ),
                        modifier = Modifier
                            .width(160.dp)
                            .height(45.dp)

                    ) {
                        Text(
                            text = "Select Roles",
                            color = Color.White
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(20.dp)
                )
                if(selectedUsers.isNotEmpty()) {
                    Text(
                        "Selected Users:"
                    )

                    Spacer(
                        Modifier.height(10.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(25.dp),
                        color = Color.White
                    ) {
                        SelectionChipFlow {
                            selectedUsers.forEach { user ->
                                SelectionFilterChip(
                                    text = user.name,
                                    onRemove = {
                                        onSelectedUsersChange(selectedUsers - user)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(
                        Modifier.height(20.dp)
                    )
                }

                if(selectedRoles.isNotEmpty()) {
                    Spacer(
                        Modifier.height(12.dp)
                    )
                    Text(
                        text = "Selected Roles:"
                    )
                    Spacer(
                        Modifier.height(8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(25.dp)
                            )
                            .background(Color.White)
                            .padding(12.dp)

                    ) {
                        SelectionChipFlow {
                            selectedRoles.forEach { role ->
                                SelectionFilterChip(
                                    text = role.name.formattedRole(),
                                    onRemove = {
                                        onSelectedRolesChange(selectedRoles - role)
                                    }
                                )
                            }
                        }
                    }
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        viewModel.selectedUsers =
                            selectedUsers
                        viewModel.selectedRoles =
                            selectedRoles.map {
                                it.name
                            }
                        viewModel.assign()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkNavy
                    )

                ) {

                    Text(
                        "Assign",
                        color = Color.White
                    )

                }

            }
        }
    }
    viewModel.activeAlert?.let { alert ->

        AlertDialog(
            onDismissRequest = {
                viewModel.clearAlert()
            },

            title = {
                Text(
                    if(alert == ActiveAlert.ERROR)
                        "Error"
                    else
                        "Success"
                )
            },

            text = {
                Text(
                    if(alert == ActiveAlert.ERROR)
                        viewModel.errorMessage
                    else
                        "Roles successfully assigned."
                )
            },

            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkNavy
                    ),
                    onClick = {
                        if(alert == ActiveAlert.SUCCESS) {
                            onSelectedUsersChange(emptySet())
                            onSelectedRolesChange(emptySet())
                        }
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