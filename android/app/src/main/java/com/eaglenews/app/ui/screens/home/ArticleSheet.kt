package com.eaglenews.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.eaglenews.app.models.Article
import com.eaglenews.app.ui.components.DefaultView
import com.eaglenews.app.ui.components.RoundButton
import com.eaglenews.app.ui.theme.DarkNavy
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.delay

@Composable
fun ArticleSheet(
    article: Article,
    onDismiss: () -> Unit
) {
    var selectedImageURL by remember { mutableStateOf<String?>(null) }
    var showImageViewer by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    var visible by remember { mutableStateOf(false) }
    var offsetY by remember { mutableStateOf(0f) }

    val animatedOffsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 1000.dp,
        animationSpec = tween(350),
        label = "sheet_slide"
    )

    LaunchedEffect(Unit) {
        visible = true
    }

    fun closeSheet() {
        visible = false
        scope.launch {
            delay(350)
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { closeSheet() }
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
                    .offset(
                        y = animatedOffsetY + offsetY.dp
                    )
                    .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
                    .background(Color.White)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                if (offsetY > 200f) closeSheet()
                                offsetY = 0f
                            },
                            onVerticalDrag = { _, dragAmount ->
                                offsetY += dragAmount
                            }
                        )
                    },
                contentAlignment = Alignment.TopCenter
            ) {
                DefaultView(
                    title = article.title,
                    showMenu = false,
                    showMenuButton = false,
                    onMenuClick = {},
                    trailing = {
                        RoundButton(
                            icon = Icons.Default.Close,
                            background = DarkNavy,
                            onClick = { closeSheet() }
                        )
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                    ) {
                        item {
                            Text(article.summary)
                            Spacer(Modifier.height(10.dp))
                        }
                        if (article.imageURLs.isNotEmpty()) {
                            item {
                                Spacer(Modifier.height(10.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(article.imageURLs.size) { index ->

                                        val url = article.imageURLs[index]

                                        SubcomposeAsyncImage(
                                            model = url,
                                            contentDescription = null,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .width(250.dp)
                                                .height(200.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    selectedImageURL = url
                                                    showImageViewer = true
                                                },
                                            loading = {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(40.dp)
                                                )
                                            },
                                            error = {
                                                Text("Failed to load image")
                                            }
                                        )
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                            }
                        }

                        item {
                            Text(
                                "Published on ${article.publishedAt?.toDate()} by ${article.publisher}"
                            )
                        }
                    }
                }
            }
        }
        if (showImageViewer && selectedImageURL != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable {
                        showImageViewer = false
                        selectedImageURL = null
                    },
                contentAlignment = Alignment.Center
            ) {

                SubcomposeAsyncImage(
                    model = selectedImageURL,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )

                IconButton(
                    onClick = {
                        showImageViewer = false
                        selectedImageURL = null
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}