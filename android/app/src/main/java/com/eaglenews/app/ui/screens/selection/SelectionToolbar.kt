package com.eaglenews.app.ui.screens.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.Olivita
import androidx.compose.ui.graphics.Color
import com.eaglenews.app.ui.theme.DefaultGray

@Composable
fun SelectionToolbar(
    searchText: String,
    onSearchChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    placeholder: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchChange,
            label = {
                Text(placeholder)
            },
            modifier = Modifier
                .weight(1f)
                .height(60.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,

                focusedIndicatorColor = DefaultGray,
                unfocusedIndicatorColor = DefaultGray,

                focusedLabelColor = DefaultGray,
                unfocusedLabelColor = DefaultGray,

                cursorColor = DefaultGray,

                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )


        Button(
            onClick = onFilterClick,
            modifier = Modifier
                .height(45.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkNavy
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 12.dp
            )
        ) {

            Text(
                text = "Select Filters",
                fontFamily = Olivita,
                color = Color.White
            )

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Filters",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 8.dp)
            )
        }
    }
}