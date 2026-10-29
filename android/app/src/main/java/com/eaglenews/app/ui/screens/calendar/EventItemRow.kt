package com.eaglenews.app.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eaglenews.app.models.EventItem
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.util.dateFormatter
import com.eaglenews.app.util.isBeforeToday
import com.eaglenews.app.util.startOfDay
import com.eaglenews.app.util.timeFormatter

/** Row content used inside EventsListView's Current/Past sections. */
@Composable
fun EventItemRow(
    item: EventItem,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    contentAlpha: Float = 1f
) {
    val eventDate = item.date
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
    Box(
        modifier = modifier
            .padding(horizontal = 5.dp)
            .clip(shape)
            .background(Color.White)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 10.dp
                )
                .alpha(contentAlpha)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy
                )
                Spacer(modifier = Modifier.weight(1f))
                if (item.pinned && !isBeforeToday(startOfDay(eventDate))) {
                    Icon(
                        imageVector = Icons.Filled.PushPin,
                        contentDescription = "Pinned",
                        tint = DarkNavy
                    )
                }
            }
            Text(
                text = eventDate?.let {
                    "${dateFormatter.format(it.toDate())} at ${timeFormatter.format(it.toDate())}"
                } ?: "",
                fontSize = 15.sp,
                color = DarkNavy
            )
        }
    }
}