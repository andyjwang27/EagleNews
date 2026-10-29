package com.eaglenews.app.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eaglenews.app.ui.theme.DarkNavy

@Composable
fun CreateButton(
    mode: (CreateMode) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Surface(
            modifier = Modifier
                .width(125.dp)
                .height(45.dp)
                .clickable {
                    expanded = !expanded
                },
            shape = RoundedCornerShape(12.dp),
            color = DarkNavy
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Create",
                    color = Color.White
                )

                Spacer(
                    Modifier.weight(1f)
                )

                Icon(
                    imageVector =
                        if (expanded)
                            Icons.Default.KeyboardArrowUp
                        else
                            Icons.Default.KeyboardArrowDown,

                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(150.dp)
                    .shadow(
                        8.dp,
                        RoundedCornerShape(16.dp)
                    )
                    .background(
                        Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp),

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            mode(CreateMode.ARTICLE)
                            expanded = false
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = DarkNavy
                    )

                    Spacer(
                        Modifier.width(8.dp)
                    )

                    Text("Article")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            mode(CreateMode.LINK)
                            expanded = false
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = DarkNavy
                    )

                    Spacer(
                        Modifier.width(8.dp)
                    )

                    Text("Link")
                }
            }
        }
    }
}