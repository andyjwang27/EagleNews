package com.eaglenews.app.ui.screens.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eaglenews.app.models.RoleItem
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.Navy
import com.eaglenews.app.ui.theme.Olivita
import java.util.Locale


@Composable
fun FilterRolesView(
    roles: List<RoleItem>,
    selectedFilters: MutableSet<String>
) {

    var isExpanded by remember {
        mutableStateOf(false)
    }


    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(230.dp)
            .background(Navy)
    ) {


        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(230.dp)
        ) {


            // Top navy area
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .background(Navy)
            )

            // Dark divider
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(DarkNavy)
            )

            // Title
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .background(Color.White),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Filters",
                    fontFamily = Olivita,
                    color = Navy
                )
            }


            // Divider
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(DarkNavy)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.width(230.dp)
            ) {
                // Role Types button
                Button(
                    onClick = {
                        isExpanded = !isExpanded
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkNavy
                    ),

                    shape = androidx.compose.foundation.shape.RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 20.dp,
                        topEnd = 20.dp
                    ),

                    contentPadding = PaddingValues(
                        horizontal = 10.dp
                    ),

                    modifier = Modifier
                        .width(170.dp)
                        .height(45.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Role Types",
                            fontFamily = Olivita,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            color = Color.White
                        )

                        Spacer(
                            Modifier.width(10.dp)
                        )
                        Icon(
                            imageVector =
                                if (isExpanded)
                                    Icons.Default.KeyboardArrowUp
                                else
                                    Icons.Default.KeyboardArrowDown,

                            contentDescription = null,

                            tint = Color.White
                        )
                    }
                }


                Spacer(
                    Modifier.height(20.dp)
                )
                if(isExpanded) {
                    val roleTypes =
                        roles
                            .map {
                                it.type
                            }
                            .distinct()
                            .sorted()
                    Column(
                        modifier = Modifier.padding(
                            start = 10.dp
                        )
                    ) {
                        roleTypes.forEach { type ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        bottom = 10.dp
                                    ),

                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {

                                        if(
                                            selectedFilters.contains(type)
                                        ) {
                                            selectedFilters.remove(type)
                                        }
                                        else {
                                            selectedFilters.add(type)
                                        }

                                    },

                                    contentPadding = PaddingValues(0.dp)
                                ) {

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector =
                                                if (
                                                    selectedFilters.contains(type)
                                                )
                                                    Icons.Default.CheckBox
                                                else
                                                    Icons.Outlined.CheckBoxOutlineBlank,

                                            contentDescription = null,

                                            tint = Color.White
                                        )
                                        Spacer(
                                            Modifier.width(10.dp)
                                        )
                                        Text(
                                            text = type.formattedRole(),
                                            fontFamily = Olivita,
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }


            Spacer(
                Modifier.weight(1f)
            )


            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(DarkNavy)
            )
        }
    }
}

fun String.formattedRole(): String =
    split(" ").joinToString(" ") {
        it.replaceFirstChar { c ->
            if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString()
        }
    }