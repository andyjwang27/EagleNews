package com.eaglenews.app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.eaglenews.app.ui.screens.register.RegisterView
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.viewmodel.AuthViewModel

@Composable
fun LoginView(
    onGoogleLogin: () -> Unit,
    onRegistrationSuccess: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    var showRegister by remember {
        mutableStateOf(false)
    }

    DefaultView(
        title = "Eagle News",
        showMenu = false,
        showMenuButton = false,
        onMenuClick = {}
    ) {
        Column {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.White)
            ) {
                Button(
                    onClick = {
                        authViewModel.beginLogin()
                        onGoogleLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 20.dp,
                            bottom = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkNavy,
                        contentColor = Color.White
                    )
                ) {
                    Text("Sign in with Google")
                }
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.White)
            ) {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(
                                top = 20.dp,
                                start = 20.dp,
                                end = 20.dp
                            ),
                        text = "Don't Have an Account?"
                    )
                    Button(
                        onClick = {
                            showRegister = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 20.dp,
                                bottom = 20.dp
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkNavy,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Register")
                    }
                }
            }
        }
    }

    if (showRegister) {
        RegisterView(
            onDismiss = {
                showRegister = false
            },
            authViewModel = authViewModel,
            onGoogleLogin = {
                onGoogleLogin()
            },
            onRegistrationSuccess = onRegistrationSuccess
        )
    }
}