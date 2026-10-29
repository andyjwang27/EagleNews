package com.eaglenews.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.eaglenews.app.models.Article
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.LightBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleRow(
    article: Article,
    isAdmin: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit
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

    SwipeRevealRowHome(
        article = article,
        isAdmin = isAdmin,
        isPinned = article.pinned,
        onPin = {
            onTogglePin()
        },
        onDelete = {
            onDelete()
        }
    ) {
        Column(
            modifier = Modifier
                .background(LightBackground)
                .padding(start = 20.dp, end = 20.dp)
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
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium
                    )


                    Text(
                        text = article.summary,
                        maxLines = 1,
                        color = Color.Gray
                    )
                }



                if (article.pinned) {

                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = DarkNavy
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
                    color = Color(0xFFE5E5EA)
                )
            }
        }
    }
}