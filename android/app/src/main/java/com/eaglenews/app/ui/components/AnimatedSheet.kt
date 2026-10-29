package com.eaglenews.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import com.eaglenews.app.ui.theme.DarkNavy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedSheet(
    title: String,
    onDismiss: () -> Unit,
    trailing: (@Composable (close: () -> Unit) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    var visible by remember { mutableStateOf(false) }
    val animatedOffsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 1000.dp,
        animationSpec = tween(350),
        label = "animated_sheet"
    )

    LaunchedEffect(Unit) { visible = true }

    fun close() {
        visible = false
        scope.launch {
            delay(350)
            onDismiss()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Dark background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { close() }
        )

        // Sheet itself
        Box(
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.90f)
                    .offset(y = animatedOffsetY)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.White)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        // Consume the click, do nothing.
                    },
                contentAlignment = Alignment.TopCenter
            ) {
                DefaultView(
                    title = title,
                    showMenu = false,
                    showMenuButton = false,
                    onMenuClick = {},
                    trailing = {
                        if (trailing != null) {
                            trailing { close() }
                        } else {
                            RoundButton(
                                icon = Icons.Default.Close,
                                background = DarkNavy,
                                onClick = { close() }
                            )
                        }
                    }
                ) {
                    content()
                }
            }
        }
    }
}