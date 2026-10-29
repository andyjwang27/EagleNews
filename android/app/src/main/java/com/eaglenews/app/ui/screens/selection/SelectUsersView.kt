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
import androidx.compose.foundation.layout.width
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
import com.eaglenews.app.models.User
import com.eaglenews.app.ui.components.AnimatedSheet
import com.eaglenews.app.ui.components.RoundButton
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.viewmodel.UsersViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun SelectUsersView(
    selected: Set<User>,
    onSelectedChange: (Set<User>) -> Unit,
    onDismiss: () -> Unit
) {

    val viewModel: UsersViewModel = viewModel()
    val roles by viewModel.roles.collectAsState()
    val users by viewModel.users.collectAsState()


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
    val currentUserId =
        FirebaseAuth.getInstance()
            .currentUser
            ?.uid

    val filteredUsers =
        users.filter { user ->
            val matchesRole =
                selectedFilters.isEmpty() ||
                        user.activeRoles.any {
                            selectedFilters.contains(it)
                        }
            val matchesSearch =
                searchText.isEmpty() ||
                        user.name.contains(
                            searchText,
                            ignoreCase = true
                        )
            matchesRole && matchesSearch
        }



    AnimatedSheet(
        title = "Select Users",
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
                text = "Users",

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
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    SelectionChipFlow {
                        filteredUsers.forEach { user ->
                            SelectionChip(
                                text =
                                    if (user.id == currentUserId)
                                        "${user.name} (You)"
                                    else
                                        user.name,

                                selected = selected.contains(user),

                                onClick = {

                                    val updatedSelection = selected.toMutableSet()
                                    if (updatedSelection.contains(user)) {
                                        updatedSelection.remove(user)
                                    } else {
                                        updatedSelection.add(user)
                                    }

                                    onSelectedChange(updatedSelection)

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
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable {
                    showFilter = false
                }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            FilterUsersView(
                roles = roles,
                selectedFilters = selectedFilters
            )
        }
    }
}