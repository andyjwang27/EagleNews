package com.eaglenews.app.ui.screens.admin

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
import com.eaglenews.app.ui.components.DefaultView
import com.eaglenews.app.ui.navigation.AppState
import com.eaglenews.app.ui.screens.selection.SelectRolesView

enum class CreateMode {
    NONE,
    ARTICLE,
    LINK
}


@Composable
fun CreateView(appState: AppState, onMenuClick: () -> Unit) {
    var showRolePicker by remember {
        mutableStateOf(false)
    }

    var selectedRoles by remember {
        mutableStateOf(setOf<RoleItem>())
    }
    var mode by remember {
        mutableStateOf(CreateMode.NONE)
    }

    DefaultView(
        title = "Create",
        showMenu = appState.showMenu,
        showMenuButton = true,
        onMenuClick = {
            appState.showMenu = !appState.showMenu
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when(mode) {
                CreateMode.ARTICLE -> {
                    AddArticleView(
                        selectedRoles = selectedRoles,
                        onSelectedRolesChange = {
                            selectedRoles = it
                        },
                        onShowRolePicker = {
                            showRolePicker = true
                        }
                    )
                }
                CreateMode.LINK -> {
                    AddLinkView(
                        selectedRoles = selectedRoles,
                        onSelectedRolesChange = {
                            selectedRoles = it
                        },
                        onShowRolePicker = {
                            showRolePicker = true
                        }
                    )
                }
                CreateMode.NONE -> {

                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 20.dp,
                        start = 10.dp
                    )
            ) {
                CreateButton(
                    mode = {
                        mode = it
                    }
                )
            }
        }
    }
    if (showRolePicker) {
        SelectRolesView(
            selected = selectedRoles,
            onSelectedChange = {
                selectedRoles = it
            },
            onDismiss = {
                showRolePicker = false
            }
        )
    }
}