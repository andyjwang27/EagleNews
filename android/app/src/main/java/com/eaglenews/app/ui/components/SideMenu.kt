package com.eaglenews.app.ui.components


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eaglenews.app.R
import com.eaglenews.app.ui.navigation.TabIcon
import com.eaglenews.app.ui.navigation.sideBarItems
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.Navy
import com.eaglenews.app.ui.theme.Olivita
import com.eaglenews.app.ui.theme.White
import com.eaglenews.app.viewmodel.SideMenuViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SideMenu(
    selectedItem: TabIcon,
    onItemSelected: (TabIcon) -> Unit,
    showMenu: Boolean,
    onClose: () -> Unit,
    isAdmin: Boolean,
    viewModel: SideMenuViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var isAnimating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val menuWidth = 265.dp
    val rowHeight = 70.dp
    val highlightWidth by animateDpAsState(
        targetValue = if (isAnimating) 7.dp else 230.dp,
        animationSpec = tween(300),
        label = "width"
    )

    LaunchedEffect(Unit) {
        viewModel.fetchUser()
    }
    if (!showMenu) return
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // DARK SCRIM (tap to close)
        if (showMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onClose()
                    }
            )
        }

        // SIDEBAR
        Box(
            modifier = Modifier
                .width(menuWidth)
                .fillMaxHeight()
                .background(Navy)
        ) {


            Column {
                // HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Navy),
                    contentAlignment = Alignment.CenterStart
                ) {


                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(White)
                    ) {


                        // TOP BAR
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .background(DarkNavy)
                        )


                        // MIDDLE CONTENT (THIS IS THE IMPORTANT PART)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(White),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Spacer(modifier = Modifier.width(20.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.logoofficial),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Eagle News",
                                        color = DarkNavy,
                                        style = TextStyle(textDecoration = TextDecoration.Underline),
                                        fontFamily = Olivita,
                                        fontSize = 18.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Welcome",
                                        color = Color(0xFFAAA9BA),
                                        fontFamily = Olivita,
                                        fontSize = 15.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center

                                    )
                                    Text(
                                        text = viewModel.userName ?: "",
                                        color = Color(0xFFAAA9BA),
                                        fontFamily = Olivita,
                                        fontSize = 15.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Spacer(modifier = Modifier.width(40.dp))
                            }
                        }


                        // BOTTOM BAR
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .background(DarkNavy)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(30.dp))


                val items = sideBarItems(isAdmin)


                items.forEach { item ->


                    val isSelected = selectedItem == item.tab


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight)
                    ) {


                        // highlight bar
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(highlightWidth)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 0.dp,
                                            topEnd = 16.dp,     // Rounds top-right
                                            bottomEnd = 16.dp,  // Rounds bottom-right
                                            bottomStart = 0.dp
                                        )
                                    )
                                    .background(DarkNavy)

                            )
                        }


                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = Color.White
                                )
                            }


                            Spacer(modifier = Modifier.width(12.dp))


                            Text(
                                text = item.title,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }


                        // click
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Transparent)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    scope.launch {

                                        // Shrink bar
                                        isAnimating = true

                                        delay(300)

                                        // Move bar
                                        onItemSelected(item.tab)

                                        delay(300)

                                        // Expand bar
                                        isAnimating = false

                                        delay(500)

                                        onClose()
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}
