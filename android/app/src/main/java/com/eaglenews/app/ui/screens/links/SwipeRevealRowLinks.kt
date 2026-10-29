package com.eaglenews.app.ui.screens.links

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.eaglenews.app.ui.theme.DarkRed
import androidx.compose.material.icons.Icons
import com.eaglenews.app.ui.components.SwipeActionButton
import com.eaglenews.app.ui.theme.LightBackground

@Composable
fun SwipeRevealRowLinks(
    isAdmin: Boolean,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {

    val scope = rememberCoroutineScope()

    val offsetX = remember {
        Animatable(0f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightBackground),
        contentAlignment = Alignment.Center
    ) {
        // DELETE BUTTON (hidden underneath)
        if (isAdmin) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(90.dp)
                    .align(Alignment.CenterEnd),
                contentAlignment = Alignment.Center
            ) {

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
        }



        // MOVING ROW
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(
                    x = offsetX.value.dp
                )
                .background(LightBackground)
                .pointerInput(isAdmin) {

                    if (isAdmin) {

                        detectHorizontalDragGestures(

                            onHorizontalDrag = { _, dragAmount ->

                                scope.launch {

                                    val newOffset =
                                        (offsetX.value + dragAmount)
                                            .coerceIn(-100f, 0f)

                                    offsetX.snapTo(newOffset)

                                }
                            },

                            onDragEnd = {

                                scope.launch {

                                    if (offsetX.value < -50f) {
                                        offsetX.animateTo(-100f)
                                    } else {
                                        offsetX.animateTo(0f)
                                    }

                                }

                            }
                        )
                    }
                }

        ) {
            content()
        }

    }
}