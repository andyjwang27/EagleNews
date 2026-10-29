package com.eaglenews.app.ui.screens.calendar

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.eaglenews.app.ui.components.SwipeActionButton
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.DarkRed
import com.eaglenews.app.ui.theme.LightBackground
import kotlinx.coroutines.launch

@Composable
fun SwipeRevealRowEvent(
    modifier: Modifier = Modifier,
    pinned: Boolean,
    showPin: Boolean,
    onEdit: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember {
        Animatable(0f)
    }
    // Width of buttons revealed
    val buttonCount = 1 + (if (showPin) 1 else 0) + 1
    val revealWidth = buttonCount * 90f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(LightBackground)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
        ) {
            SwipeActionButton(
                icon = Icons.Default.Edit,
                label = "Edit",
                color = DarkNavy,
                onClick = {
                    onEdit()

                    scope.launch {
                        offsetX.animateTo(0f)
                    }
                }
            )
            Spacer(modifier = Modifier.width(5.dp))
            if (showPin) {
                SwipeActionButton(
                    icon =
                        if (pinned)
                            Icons.Default.RemoveCircleOutline
                        else
                            Icons.Default.PushPin,
                    label =
                        if (pinned)
                            "Unpin"
                        else
                            "Pin",
                    color = DarkNavy,
                    onClick = {
                        onTogglePin()

                        scope.launch {
                            offsetX.animateTo(0f)
                        }
                    }
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
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
                .background(LightBackground)
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
                                if (offsetX.value < -(revealWidth / 2)) {
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