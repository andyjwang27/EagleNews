package com.eaglenews.app.ui.screens.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eaglenews.app.models.RoleItem
import com.eaglenews.app.models.User
import com.eaglenews.app.ui.components.AnimatedSheet
import com.eaglenews.app.ui.components.RoundButton
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.viewmodel.RolesViewModel


@Composable
fun SelectUserRolesView(
    selected: Set<RoleItem>,
    selectedUser: User?,
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



    val userRoles =
        remember(
            roles,
            selectedUser
        ) {

            if(selectedUser == null) {
                emptyList()
            }
            else {
                roles.filter { role ->
                    selectedUser.activeRoles.contains(
                        role.name
                    )
                }
            }
        }



    val filteredRoles =
        userRoles.filter { role ->

            val matchesSearch =
                searchText.isEmpty() ||
                        role.name.contains(
                            searchText,
                            ignoreCase = true
                        )


            val matchesFilter =
                selectedFilters.isEmpty() ||
                        selectedFilters.contains(
                            role.name
                        )


            matchesSearch && matchesFilter
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
                    10.dp
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
                Modifier.height(15.dp)
            )



            if(selectedFilters.isNotEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(25.dp)
                        )
                        .background(Color.White)
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
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(25.dp)
                        )
                        .background(Color.White)
                        .padding(12.dp)

                ) {


                    SelectionChipFlow {


                        filteredRoles.forEach { role ->


                            SelectionChip(

                                text = role.name.formattedRole(),

                                selected =
                                    selected.contains(role),


                                onClick = {


                                    val updated =
                                        selected.toMutableSet()


                                    if(updated.contains(role)) {

                                        updated.remove(role)

                                    }
                                    else {

                                        updated.add(role)

                                    }


                                    onSelectedChange(
                                        updated
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



    if(showFilter) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(
                        alpha = .5f
                    )
                )
                .clickable {
                    showFilter = false
                }
        )


        FilterRolesView(
            roles = roles,
            selectedFilters = selectedFilters
        )

    }

}