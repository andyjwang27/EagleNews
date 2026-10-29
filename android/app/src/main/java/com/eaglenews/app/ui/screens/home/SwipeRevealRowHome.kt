package com.eaglenews.app.ui.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.DarkRed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RemoveCircleOutline
import com.eaglenews.app.models.Article
import androidx.compose.material.icons.filled.Delete
import com.eaglenews.app.ui.components.SwipeActionButton
import com.eaglenews.app.ui.theme.LightBackground

@Composable
fun SwipeRevealRowHome(
    article: Article,
    isAdmin: Boolean,
    isPinned: Boolean,
    onPin: () -> Unit,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember {
        Animatable(0f)
    }
    // Width of buttons revealed
    val revealWidth = if (isAdmin) 180f else 90f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightBackground)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(IntrinsicSize.Min)
        ) {
            if (isAdmin) {
                SwipeActionButton(
                    icon =
                        if (article.pinned)
                            Icons.Default.RemoveCircleOutline
                        else
                            Icons.Default.PushPin,
                    label =
                        if (article.pinned)
                            "Unpin"
                        else
                            "Pin",
                    color = DarkNavy,
                    onClick = {
                        onPin()

                        scope.launch {
                            offsetX.animateTo(0f)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            SwipeActionButton(
                icon = Icons.Default.Delete,
                label = "Delete",
                color = DarkRed,

                onClick = {
                    onDelete()

                    scope.launch {
                        offsetX.animateTo(0f)
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(
                    x = offsetX.value.dp
                )
                .background(Color.White)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val newOffset =
                                    (offsetX.value + dragAmount)
                                        .coerceIn(
                                            -revealWidth,
                                            0f
                                        )
                                offsetX.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                if(offsetX.value < -(revealWidth / 2)) {
                                    offsetX.animateTo(
                                        -revealWidth
                                    )
                                } else {
                                    offsetX.animateTo(
                                        0f
                                    )
                                }
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}