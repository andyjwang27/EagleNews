package com.eaglenews.app.ui.screens.todo

import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eaglenews.app.models.ToDoListItem
import com.eaglenews.app.models.sortPinnedAndDueDate
import com.eaglenews.app.ui.components.DefaultView
import com.eaglenews.app.ui.components.RoundButton
import com.eaglenews.app.ui.theme.DarkNavy
import com.eaglenews.app.ui.theme.DarkRed
import java.util.Calendar
import java.util.Date

// Shared list body used by all three screens
@Composable
private fun ToDoListContent(
    primaryHeader: String,
    primaryItems: List<ToDoListItem>,
    showEditOnPrimary: Boolean,
    overdueItems: List<ToDoListItem>,
    completedItems: List<ToDoListItem>,
    onEdit: (ToDoListItem) -> Unit,
    onTogglePin: (ToDoListItem) -> Unit,
    onDeleteRequest: (ToDoListItem) -> Unit,
    onToggleCheck: (ToDoListItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (primaryItems.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = primaryHeader,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .height(1.dp)

                    )
                }
            }
            itemsIndexed(
                items = primaryItems,
                key = { _, item -> item.id }
            ) { index, item ->
                SwipeRevealRowToDo(
                    modifier = Modifier.animateItem(
                        fadeInSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = 400f
                        ),
                        fadeOutSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = 400f
                        ),
                        placementSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = 400f
                        )
                    ),
                    item = item,
                    showEdit = showEditOnPrimary,
                    showPin = true,
                    onEdit = { onEdit(item) },
                    onTogglePin = { onTogglePin(item) },
                    onDelete = { onDeleteRequest(item) }
                ) {
                    ToDoListItemRow(
                        item = item,
                        isFirst = index == 0,
                        isLast = index == primaryItems.lastIndex,
                        onToggleCheck = { onToggleCheck(item) }
                    )                }
            }
        }

        if (overdueItems.isNotEmpty()) {
            item {
                Text(
                    text = "Overdue",
                    color = DarkRed,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            itemsIndexed(
                items = overdueItems,
                key = { _, item -> item.id }
            ) { index, item ->
                SwipeRevealRowToDo(
                    modifier = Modifier.animateItem(
                        placementSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = 400f
                        )
                    ),
                    item = item,
                    showEdit = false,
                    showPin = true,
                    onEdit = {},
                    onTogglePin = { onTogglePin(item) },
                    onDelete = { onDeleteRequest(item) }
                ) {
                    ToDoListItemRow(
                        item = item,
                        isFirst = index == 0,
                        isLast = index == overdueItems.lastIndex,
                        onToggleCheck = { onToggleCheck(item) }
                    )
                }
            }
        }

        if (completedItems.isNotEmpty()) {
            item {
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            itemsIndexed(
                items = completedItems,
                key = { _, item -> item.id }
            ) { index, item ->
                SwipeRevealRowToDo(
                    modifier = Modifier.animateItem(
                        placementSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = 400f
                        )
                    ),
                    item = item,
                    showEdit = false,
                    showPin = false,
                    onEdit = {},
                    onTogglePin = {},
                    onDelete = { onDeleteRequest(item) }
                ) {
                    Box(modifier = Modifier.alpha(0.4f)) {
                        ToDoListItemRow(
                            item = item,
                            isFirst = index == 0,
                            isLast = index == completedItems.lastIndex,
                            onToggleCheck = { onToggleCheck(item) }
                        )                    }
                }
            }
        }
    }
}

// Shared chrome: header, delete confirmation alert, and new/edit sheet.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToDoScaffold(
    title: String,
    showMenu: Boolean,
    onMenuClick: () -> Unit,
    viewModel: ToDoListViewModel,
    body: @Composable () -> Unit
) {
    DefaultView(
        title = title,
        showMenu = showMenu,
        showMenuButton = true,
        onMenuClick = onMenuClick,
        trailing = {
            RoundButton(
                icon = Icons.Filled.Add,
                background = DarkNavy,
                onClick = { viewModel.todoSheet = ToDoSheet.NEW_ITEM }
            )
        }
    ) {
        body()
    }

    viewModel.todoToDelete?.let { todo ->
        AlertDialog(
            onDismissRequest = { viewModel.todoToDelete = null },
            title = { Text("Delete Todo?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                Button(
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = DarkRed
                        ),
                    onClick = {
                        viewModel.delete(todo.id)
                        viewModel.todoToDelete = null
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.todoToDelete = null }) {
                    Text(
                        text ="Cancel",
                        color = Color.White
                    )
                }
            }
        )
    }

    if (viewModel.todoSheet != null) {
        when (viewModel.todoSheet) {
            ToDoSheet.NEW_ITEM -> {
                NewItemView(
                    onDismiss = { viewModel.todoSheet = null }
                )
            }

            ToDoSheet.EDIT_TODO -> {
                val item = viewModel.selectedToDo
                if (item != null) {
                    EditToDoView(
                        selectedToDo = item,
                        onDismiss = { viewModel.todoSheet = null }
                    )
                }
            }

            null -> {}
        }
    }
}

// ToDoTodayView
@Composable
fun ToDoTodayView(
    viewModel: ToDoListViewModel,
    showMenu: Boolean,
    onMenuClick: () -> Unit
) {
    val items by viewModel.items.collectAsState()

    val completedItems = remember(items) {
        val calendar = Calendar.getInstance()
        val startOfToday = startOfDay(Date())
        items.filter { item ->
            val itemDate = Date((item.dueDate * 1000).toLong())
            item.isDone && (isSameDay(itemDate, Date()) || itemDate.before(startOfToday))
        }.sortPinnedAndDueDate()
    }
    val completedIds = remember(completedItems) { completedItems.map { it.id }.toSet() }

    val todaysItems = remember(items, completedIds) {
        items.filter { item ->
            val itemDate = Date((item.dueDate * 1000).toLong())
            isSameDay(itemDate, Date()) && item.id !in completedIds
        }.sortPinnedAndDueDate()
    }

    val overDueItems = remember(items, completedIds) {
        val startOfToday = startOfDay(Date())
        items.filter { item ->
            val itemDate = Date((item.dueDate * 1000).toLong())
            itemDate.before(startOfToday) && item.id !in completedIds
        }.sortPinnedAndDueDate()
    }

    ToDoScaffold("To-Do List", showMenu, onMenuClick, viewModel) {
        ToDoListContent(
            primaryHeader = "Today's Items",
            primaryItems = todaysItems,
            showEditOnPrimary = true,
            overdueItems = overDueItems,
            completedItems = completedItems,
            onEdit = { viewModel.selectedToDo = it; viewModel.todoSheet = ToDoSheet.EDIT_TODO },
            onTogglePin = { viewModel.togglePin(it) },
            onDeleteRequest = { viewModel.todoToDelete = it },
            onToggleCheck = { viewModel.toggleCheck(it) }
        )
    }
}

// ToDoWeekView
@Composable
fun ToDoWeekView(
    viewModel: ToDoListViewModel,
    showMenu: Boolean,
    onMenuClick: () -> Unit
) {
    val items by viewModel.items.collectAsState()

    val completedItems = remember(items) {
        val startOfToday = startOfDay(Date())
        val oneWeekFromToday = addDays(startOfToday, 7)
        items.filter { item ->
            val itemDate = Date((item.dueDate * 1000).toLong())
            item.isDone && ((itemDate >= startOfToday && itemDate < oneWeekFromToday) || itemDate.before(startOfToday))
        }.sortPinnedAndDueDate()
    }
    val completedIds = remember(completedItems) { completedItems.map { it.id }.toSet() }

    val weekItems = remember(items, completedIds) {
        val startOfToday = startOfDay(Date())
        val oneWeekFromToday = addDays(startOfToday, 7)
        items.filter { item ->
            val itemDate = Date((item.dueDate * 1000).toLong())
            itemDate >= startOfToday && itemDate < oneWeekFromToday && item.id !in completedIds
        }.sortPinnedAndDueDate()
    }

    val overDueItems = remember(items, completedIds) {
        val startOfToday = startOfDay(Date())
        items.filter { item ->
            val itemDate = Date((item.dueDate * 1000).toLong())
            itemDate.before(startOfToday) && item.id !in completedIds
        }.sortPinnedAndDueDate()
    }

    ToDoScaffold("To-Do List", showMenu, onMenuClick, viewModel) {
        ToDoListContent(
            primaryHeader = "This Week's Items",
            primaryItems = weekItems,
            showEditOnPrimary = true,
            overdueItems = overDueItems,
            completedItems = completedItems,
            onEdit = { viewModel.selectedToDo = it; viewModel.todoSheet = ToDoSheet.EDIT_TODO },
            onTogglePin = { viewModel.togglePin(it) },
            onDeleteRequest = { viewModel.todoToDelete = it },
            onToggleCheck = { viewModel.toggleCheck(it) }
        )
    }
}

// ToDoAllView
@Composable
fun ToDoAllView(
    viewModel: ToDoListViewModel,
    showMenu: Boolean,
    onMenuClick: () -> Unit
) {
    val items by viewModel.items.collectAsState()

    val completedItems = remember(items) {
        items.filter { it.isDone }.sortPinnedAndDueDate()
    }
    val completedIds = remember(completedItems) { completedItems.map { it.id }.toSet() }

    val allItems = remember(items, completedIds) {
        val startOfToday = startOfDay(Date())
        items.filter { item ->
            val itemDate = Date((item.dueDate * 1000).toLong())
            itemDate >= startOfToday && item.id !in completedIds
        }.sortPinnedAndDueDate()
    }

    val overDueItems = remember(items, completedIds) {
        val startOfToday = startOfDay(Date())
        items.filter { item ->
            val itemDate = Date((item.dueDate * 1000).toLong())
            itemDate.before(startOfToday) && item.id !in completedIds
        }.sortPinnedAndDueDate()
    }

    ToDoScaffold("To-Do List", showMenu, onMenuClick, viewModel) {
        ToDoListContent(
            primaryHeader = "All Items",
            primaryItems = allItems,
            showEditOnPrimary = true,
            overdueItems = overDueItems,
            completedItems = completedItems,
            onEdit = { viewModel.selectedToDo = it; viewModel.todoSheet = ToDoSheet.EDIT_TODO },
            onTogglePin = { viewModel.togglePin(it) },
            onDeleteRequest = { viewModel.todoToDelete = it },
            onToggleCheck = { viewModel.toggleCheck(it) }
        )
    }
}

// Date helpers
private fun startOfDay(date: Date): Date =
    Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

private fun addDays(date: Date, days: Int): Date =
    Calendar.getInstance().apply {
        time = date
        add(Calendar.DAY_OF_YEAR, days)
    }.time

private fun isSameDay(a: Date, b: Date): Boolean {
    val calA = Calendar.getInstance().apply { time = a }
    val calB = Calendar.getInstance().apply { time = b }
    return calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) &&
            calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR)
}