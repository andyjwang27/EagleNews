package com.eaglenews.app.ui.screens.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eaglenews.app.models.ToDoListItem
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.Navy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ToDoListItemRow(
    item: ToDoListItem,
    isFirst: Boolean,
    isLast: Boolean,
    onToggleCheck: () -> Unit
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(
                    vertical = 6.dp
                )
        ) {
            IconButton(onClick = onToggleCheck) {
                Icon(
                    imageVector = if (item.isDone) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                    contentDescription = if (item.isDone) "Mark as not done" else "Mark as done",
                    tint = Navy,
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = formatDueDate(item.dueDate),
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            if (item.pinned && !item.isDone) {
                Icon(
                    imageVector = Icons.Filled.PushPin,
                    contentDescription = "Pinned",
                    tint = DarkNavy,
                    modifier = Modifier.padding(end = 10.dp)
                )
            }
        }
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = 1.dp,
                color = Color(0xFFE5E5EA)
            )
        }
    }
}

fun formatDueDate(dueDate: Double): String {
    val date = Date((dueDate * 1000).toLong())
    val formatter = SimpleDateFormat("MMMM d, h:mm a", Locale.getDefault())
    return formatter.format(date)
}