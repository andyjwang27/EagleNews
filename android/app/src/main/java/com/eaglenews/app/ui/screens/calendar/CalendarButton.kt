package com.eaglenews.app.ui.screens.calendar

import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eaglenews.app.ui.components.RoundButton
import com.eaglenews.app.ui.theme.DarkNavy
import androidx.compose.ui.graphics.vector.ImageVector
import com.eaglenews.app.ui.theme.Olivita

@Composable
fun CalendarButton(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = Olivita,
            color = DarkNavy
        )

        Spacer(Modifier.height(8.dp))

        RoundButton(
            icon = icon,
            background = DarkNavy,
            onClick = onClick
        )
    }
}