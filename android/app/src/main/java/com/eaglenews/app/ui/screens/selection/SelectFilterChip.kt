package com.eaglenews.app.ui.screens.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.LightBackground

@Composable
fun SelectionFilterChip(
    text: String,
    modifier: Modifier = Modifier,
    onRemove: () -> Unit
) {
    Row(
        modifier = modifier
            .background(
                color = LightBackground,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove Filter",
            tint = DarkNavy,
            modifier = Modifier
                .clickable(onClick = onRemove)
        )
    }
}