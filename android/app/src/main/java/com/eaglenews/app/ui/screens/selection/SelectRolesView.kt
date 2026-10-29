package com.eaglenews.app.ui.screens.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.models.RoleItem
import com.eaglenews.app.ui.components.AnimatedSheet
import com.eaglenews.app.ui.components.RoundButton
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.viewmodel.RolesViewModel


@Composable
fun SelectRolesView(
    selected: Set<RoleItem>,
    onSelectedChange: (Set<RoleItem>) -> Unit,
    onDismiss: () -> Unit
) {

    val viewModel: RolesViewModel = viewModel()


    val roles by viewModel.roles.collectAsState()


    var searchText by remember {
        mutableStateOf("")
    }


    var showFilter by remember {
        mutableStateOf(false)
    }


    val selectedFilters =
        remember {
            mutableStateSetOf<String>()
        }



    val filteredRoles =
        roles.filter { role ->


            val matchesType =
                selectedFilters.isEmpty() ||
                        selectedFilters.contains(
                            role.type
                        )


            val matchesSearch =
                searchText.isEmpty() ||
                        role.name.contains(
                            searchText,
                            ignoreCase = true
                        )


            matchesType && matchesSearch
        }




    AnimatedSheet(
        title = "Select Roles",

        onDismiss = onDismiss,

        trailing = { close ->

            RoundButton(
                icon = Icons.Default.Close,
                background = DarkNavy,
                onClick = close
            )
        }

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 10.dp,
                    start = 10.dp,
                    end = 10.dp
                )

        ) {
            SelectionToolbar(
                searchText = searchText,

                onSearchChange = {
                    searchText = it
                },

                onFilterClick = {
                    showFilter = true
                },

                placeholder = "Search Roles"
            )
            Spacer(
                Modifier.height(12.dp)
            )
            if(selectedFilters.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    SelectionChipFlow {
                        selectedFilters.forEach { filter ->
                            SelectionFilterChip(
                                text = filter.formattedRole(),
                                onRemove = {
                                    selectedFilters.remove(
                                        filter
                                    )

                                }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    }
                }
            }

            Spacer(
                Modifier.height(20.dp)
            )
            Text(
                text = "Roles",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(
                Modifier.height(12.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color.White)
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color.White)
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        SelectionChipFlow {
                            filteredRoles.forEach { role ->
                                SelectionChip(
                                    text = role.name.formattedRole(),
                                    selected =
                                        selected.contains(role),
                                    onClick = {
                                        onSelectedChange(
                                            if (selected.contains(role)) {
                                                selected - role
                                            } else {
                                                selected + role
                                            }
                                        )

                                    }
                                )
                                Spacer(
                                    Modifier.height(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if(showFilter) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable {
                    showFilter = false
                }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            FilterRolesView(
                roles = roles,
                selectedFilters = selectedFilters
            )
        }
    }
}