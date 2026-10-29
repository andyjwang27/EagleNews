package com.eaglenews.app.ui.screens.admin

import SingleSelectUserView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eaglenews.app.models.RoleItem
import com.eaglenews.app.models.User
import com.eaglenews.app.ui.components.DefaultView
import com.eaglenews.app.ui.navigation.AppState
import com.eaglenews.app.ui.screens.selection.SelectRolesView
import com.eaglenews.app.ui.screens.selection.SelectUserRolesView
import com.eaglenews.app.ui.screens.selection.SelectUsersView

@Composable
fun ChangeRolesView(
    appState: AppState,
    onMenuClick: () -> Unit
) {
    var showUsersPicker by remember {
        mutableStateOf(false)
    }
    var showRolesPicker by remember {
        mutableStateOf(false)
    }
    var selectedUsers by remember {
        mutableStateOf(setOf<User>())
    }
    var selectedRoles by remember {
        mutableStateOf(setOf<RoleItem>())
    }
    var mode by remember {
        mutableStateOf(AssignMode.NONE)
    }
    var selectedUser by remember {
        mutableStateOf<User?>(null)
    }

    var selectedRemoveRoles by remember {
        mutableStateOf(setOf<RoleItem>())
    }

    var showSingleUserPicker by remember {
        mutableStateOf(false)
    }

    var showUserRolesPicker by remember {
        mutableStateOf(false)
    }

    DefaultView(
        title = "Change Roles",
        showMenu = appState.showMenu,
        showMenuButton = true,
        onMenuClick = {
            appState.showMenu = !appState.showMenu
        }
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            when (mode) {

                AssignMode.ASSIGN -> {
                    AssignRolesView(
                        selectedUsers = selectedUsers,
                        selectedRoles = selectedRoles,
                        onSelectedUsersChange = {
                            selectedUsers = it
                        },
                        onSelectedRolesChange = {
                            selectedRoles = it
                        },
                        onShowUsersPicker = {
                            showUsersPicker = true
                        },
                        onShowRolePicker = {
                            showRolesPicker = true
                        }
                    )
                }

                AssignMode.REMOVE -> {
                    RemoveRolesView(

                        selectedUser = selectedUser,

                        selectedRoles = selectedRemoveRoles,


                        onSelectedUserChange = {
                            selectedUser = it
                        },


                        onSelectedRolesChange = {
                            selectedRemoveRoles = it
                        },


                        onShowUserPicker = {
                            showSingleUserPicker = true
                        },


                        onShowRolesPicker = {
                            showUserRolesPicker = true
                        }

                    )

                }

                AssignMode.NONE -> {}
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 20.dp,
                        start = 10.dp
                    )
            ) {

                AssignButton(
                    mode = {
                        mode = it
                    }
                )
            }
        }
    }

    if (showUsersPicker) {
        SelectUsersView(
            selected = selectedUsers,
            onSelectedChange = {
                selectedUsers = it
            },
            onDismiss = {
                showUsersPicker = false
            }
        )
    }

    if (showRolesPicker) {
        SelectRolesView(
            selected = selectedRoles,
            onSelectedChange = {
                selectedRoles = it
            },
            onDismiss = {
                showRolesPicker = false
            }
        )
    }
    if(showSingleUserPicker){

        SingleSelectUserView(

            selected = selectedUser,

            onSelectedChange = {
                selectedUser = it
            },

            onDismiss = {
                showSingleUserPicker = false
            }

        )

    }

    if(showUserRolesPicker){

        SelectUserRolesView(

            selected = selectedRemoveRoles,

            selectedUser = selectedUser,

            onSelectedChange = {
                selectedRemoveRoles = it
            },

            onDismiss = {
                showUserRolesPicker = false
            }

        )

    }
}