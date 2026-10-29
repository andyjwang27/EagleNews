package com.eaglenews.app.ui.screens.selection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eaglenews.app.models.RoleItem
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.Olivita
import com.eaglenews.app.ui.theme.Navy
import java.util.Locale

@Composable
fun FilterSectionView(
    title: String,
    roles: List<RoleItem>,
    expandedSections: MutableSet<String>,
    selectedFilters: MutableSet<String>
) {

    val expanded =
        expandedSections.contains(title)


    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {


        // Header button
        Row(
            modifier = Modifier
                .width(170.dp)
                .height(45.dp)
                .clip(
                    RoundedCornerShape(
                        topEnd = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .background(DarkNavy)
                .clickable {

                    if (expanded) {
                        expandedSections.remove(title)
                    } else {
                        expandedSections.add(title)
                    }

                }
                .padding(horizontal = 10.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = title,
                fontFamily = Olivita,
                color = Color.White
            )


            Spacer(
                Modifier.width(10.dp)
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


        // Options
        if (expanded) {

            Column(
                modifier = Modifier
                    .padding(
                        start = 20.dp,
                        top = 10.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                roles.forEach { role ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {


                                if (
                                    selectedFilters.contains(role.name)
                                ) {
                                    selectedFilters.remove(role.name)
                                } else {
                                    selectedFilters.add(role.name)
                                }

                            },

                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        Icon(
                            imageVector =
                                if (
                                    selectedFilters.contains(role.name)
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
                            text = role.name.formattedRole(),
                            fontFamily = Olivita,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}