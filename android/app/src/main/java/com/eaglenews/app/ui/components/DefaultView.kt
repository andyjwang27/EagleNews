package com.eaglenews.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.LightBackground
import com.eaglenews.app.ui.theme.Navy
import com.eaglenews.app.ui.theme.Olivita

@Composable
fun DefaultView(
    title: String,
    showMenu: Boolean,
    showMenuButton: Boolean,
    onMenuClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {

    var reveal by remember { mutableStateOf(false) }

    val titleWidth by animateDpAsState(
        targetValue = if (reveal) 280.dp else 0.dp,
        animationSpec = tween(durationMillis = 1200),
        label = "TitleReveal"
    )

    LaunchedEffect(Unit) {
        reveal = true
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ================= MAIN SCREEN =================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {

            // ================= HEADER =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Navy)
            ) {

                Column {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(DarkNavy)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(DarkNavy)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // LEFT MENU BUTTON
                    Box(
                        modifier = Modifier.width(50.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (showMenuButton) {
                            IconButton(
                                onClick = onMenuClick,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(DarkNavy)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // CENTER TITLE
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(titleWidth)
                                .clipToBounds(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontFamily = Olivita,
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // RIGHT SLOT
                    Box(
                        modifier = Modifier.width(50.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        trailing?.invoke()
                    }
                }
            }

            // ================= CONTENT =================
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(LightBackground)
                    .fillMaxWidth(),
                content = content
            )

            // ================= BOTTOM BAR =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Navy)
            ) {
                Column {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(DarkNavy)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(DarkNavy)
                    )
                }
            }
        }

        // ================= OPTIONAL SCRIM =================
        if (showMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
        }
    }
}