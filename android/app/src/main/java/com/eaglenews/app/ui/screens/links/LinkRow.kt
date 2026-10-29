package com.eaglenews.app.ui.screens.links

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eaglenews.app.models.Link
import com.eaglenews.app.ui.theme.LightBackground

@Composable
fun LinkRow(
    link: Link,
    isAdmin: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val shape = when {
        isFirst && isLast -> RoundedCornerShape(25.dp)

        isFirst -> RoundedCornerShape(
            topStart = 25.dp,
            topEnd = 25.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )

        isLast -> RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 25.dp,
            bottomEnd = 25.dp
        )

        else -> RoundedCornerShape(0.dp)
    }
    SwipeRevealRowLinks(
        isAdmin = isAdmin,
        onDelete = onDelete
    ) {
        Column(
            modifier = Modifier
                .background(LightBackground)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(Color.White)
                    .clickable {
                        onClick()
                    }
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = link.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )

                    Text(
                        text = link.link,
                        maxLines = 2
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            if (!isLast) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFE5E5EA),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}