package com.eaglenews.app.ui.screens.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eaglenews.app.ui.theme.DefaultGray
import com.eaglenews.app.ui.theme.LightBackground

@Composable
fun RoleChip(
    text: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = LightBackground,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
        )
    }
}