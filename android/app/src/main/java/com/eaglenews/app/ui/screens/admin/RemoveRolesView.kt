package com.eaglenews.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
fun RemoveRolesView(
    selectedUser: User?,
    selectedRoles: Set<RoleItem>,
    onSelectedUserChange: (User?) -> Unit,
    onSelectedRolesChange: (Set<RoleItem>) -> Unit,
    onShowUserPicker: () -> Unit,
    onShowRolesPicker: () -> Unit

) {
    val viewModel: RemoveRolesViewViewModel = viewModel()

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
            Modifier.height(75.dp)
        )
        Text(
            text = "Remove User's Roles",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(
            Modifier.height(16.dp)
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
                Text(
                    "Select User:"
                )
                Spacer(
                    Modifier.height(10.dp)
                )
                Button(
                    onClick = onShowUserPicker,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkNavy
                    ),
                    modifier = Modifier
                        .width(160.dp)
                        .height(45.dp)
                ) {
                    Text(
                        "Select User",
                        color = Color.White
                    )
                }

                if(selectedUser != null) {
                    Spacer(
                        Modifier.height(15.dp)
                    )
                    Text(
                        "Selected User:"
                    )
                    Spacer(
                        Modifier.height(8.dp)
                    )
                    SelectionFilterChip(
                        text = selectedUser.name,
                        onRemove = {
                            onSelectedUserChange(null)
                        }

                    )
                    Spacer(
                        Modifier.height(20.dp)
                    )
                    Text(
                        "Select Roles to Delete:"
                    )
                    Spacer(
                        Modifier.height(10.dp)
                    )
                    Button(
                        onClick = onShowRolesPicker,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkNavy
                        ),
                        modifier = Modifier
                            .width(160.dp)
                            .height(45.dp)
                    ) {
                        Text(
                            "Select Roles",
                            color = Color.White
                        )
                    }

                    if(selectedRoles.isNotEmpty()) {
                        Spacer(
                            Modifier.height(15.dp)
                        )
                        Text(
                            "Selected Roles:"
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
                                .background(
                                    Color.White
                                )
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
                        Modifier.height(25.dp)
                    )
                    Button(
                        onClick = {
                            selectedUser.let { user ->
                                viewModel.removeRoles(
                                    from = user,
                                    rolesToDelete =
                                        selectedRoles.map {
                                            it.name
                                        }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkNavy
                        )
                    ) {
                        Text(
                            "Remove",
                            color = Color.White
                        )
                    }
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
                        viewModel.successMessage
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkNavy
                    ),
                    onClick = {
                        if(alert == ActiveAlert.SUCCESS) {
                            onSelectedUserChange(null)
                            onSelectedRolesChange(
                                emptySet()
                            )
                            viewModel.resetSuccess()
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