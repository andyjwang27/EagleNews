package com.eaglenews.app.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.ui.components.DefaultView
import com.eaglenews.app.ui.components.RoundButton
import com.eaglenews.app.ui.navigation.AppState
import com.eaglenews.app.ui.theme.Arial
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileView(
    appState: AppState,
    authViewModel: AuthViewModel,
    viewModel: ProfileViewViewModel = viewModel()
) {
    val user = viewModel.user
    var showSettings by remember { mutableStateOf(false) }
    val joinedDate = remember(user?.joined) {
        user?.joined?.let {
            SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                .format(Date(it * 1000))
        } ?: "Unknown"
    }

    Text(
        text = joinedDate,
        fontFamily = Arial,
        textAlign = TextAlign.Center
    )

    LaunchedEffect(Unit) {
        viewModel.fetchUser()
    }

    DefaultView(
        title = "Profile",
        showMenu = appState.showMenu,
        showMenuButton = true,
        onMenuClick = { appState.showMenu = !appState.showMenu },
        trailing = {
            RoundButton(
                icon = Icons.Default.Settings,
                background = DarkNavy
            ) {
                showSettings = true
            }
        }
    ) {
        if (user != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        modifier = Modifier.size(120.dp),
                        tint = DarkNavy,
                        contentDescription = "Profile",
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(start = 30.dp)
                ) {
                    Text(
                        text = "Name: ",
                        fontFamily = Arial,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = user.name,
                        fontFamily = Arial,
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(start = 30.dp)
                ) {
                    Text(
                        text = "Email: ",
                        fontFamily = Arial,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = user.email,
                        fontFamily = Arial,
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(start = 30.dp)
                ) {
                    Text(
                        text = "Member Since: ",
                        fontFamily = Arial,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = joinedDate,
                        fontFamily = Arial,
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(start = 30.dp)
                ) {
                    Text(
                        text = "Roles: ",
                        fontFamily = Arial,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = user.activeRoles.joinToString(", ") { it.formattedRole() },
                        fontFamily = Arial,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Loading Profile...")
            }
        }
    }

    if (showSettings) {
        ProfileSettingsView(
            onDismiss = { showSettings = false },
            authViewModel = authViewModel
        )
    }
}

fun String.formattedRole(): String =
    split(" ").joinToString(" ") {
        it.replaceFirstChar { c ->
            if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString()
        }
    }