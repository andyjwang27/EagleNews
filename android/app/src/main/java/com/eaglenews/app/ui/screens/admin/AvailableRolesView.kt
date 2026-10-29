package com.eaglenews.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.ui.components.DefaultView
import com.eaglenews.app.ui.navigation.AppState
import com.eaglenews.app.ui.theme.DefaultGray
import com.eaglenews.app.viewmodel.RolesViewModel
import java.util.Locale

@Composable
fun AvailableRolesView(
    appState: AppState,
    onMenuClick: () -> Unit
) {
    val viewModel: RolesViewModel = viewModel()
    val roles by viewModel.roles.collectAsState()
    var searchText by remember {
        mutableStateOf("")
    }
    val filteredRoles = remember(searchText, roles) {
        if (searchText.isBlank()) {
            roles
        } else {
            roles.filter {
                it.name.contains(
                    searchText,
                    ignoreCase = true
                )
            }
        }
    }
    DefaultView(
        title = "Available Roles",
        showMenu = appState.showMenu,
        showMenuButton = true,
        onMenuClick = onMenuClick
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(
                Modifier.height(16.dp)
            )
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = {
                    Text("Search Roles")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
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
                text = "Available Roles: ${filteredRoles.size}"
            )

            Spacer(
                Modifier.height(8.dp)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.White)
                    .padding(12.dp)
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    filteredRoles.forEach { role ->
                        RoleChip(
                            text = role.name.formattedRole(),
                        )
                    }
                }
            }
        }
    }
}
fun String.formattedRole(): String =
    split(" ").joinToString(" ") {
        it.replaceFirstChar { c ->
            if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString()
        }
    }