package com.eaglenews.app.ui.screens.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eaglenews.app.models.RoleItem
import com.eaglenews.app.ui.screens.selection.FilterSectionView
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.Navy
import com.eaglenews.app.ui.theme.Olivita


@Composable
fun FilterUsersView(
    roles: List<RoleItem>,
    selectedFilters: MutableSet<String>
) {

    val expandedSections =
        remember {
            mutableStateSetOf<String>()
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
            // Top navy spacer
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
            Column(
                modifier = Modifier
                    .width(230.dp)
                    .padding(top = 20.dp)
            ) {


                FilterSectionView(
                    title = "Grades",

                    roles = roles.filter {
                        it.type == "gradeLevel"
                    },

                    expandedSections = expandedSections,

                    selectedFilters = selectedFilters
                )


                Spacer(
                    Modifier.height(20.dp)
                )


                FilterSectionView(
                    title = "Clubs",

                    roles = roles.filter {
                        it.type == "club"
                    },

                    expandedSections = expandedSections,

                    selectedFilters = selectedFilters
                )


                Spacer(
                    Modifier.height(20.dp)
                )


                FilterSectionView(
                    title = "Sports",

                    roles = roles.filter {
                        it.type == "sports"
                    },

                    expandedSections = expandedSections,

                    selectedFilters = selectedFilters
                )


                Spacer(
                    Modifier.height(20.dp)
                )


                FilterSectionView(
                    title = "Priority",

                    roles = roles.filter {
                        it.type == "priorityLevel"
                    },

                    expandedSections = expandedSections,

                    selectedFilters = selectedFilters
                )
            }


            Spacer(
                Modifier.weight(1f)
            )


            // Bottom divider
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(DarkNavy)
            )
        }
    }
}