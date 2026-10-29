package com.eaglenews.app.ui.navigation
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

data class SideBarModel(
    val icon: ImageVector,
    val title: String,
    val tab: TabIcon,
    val index: Int
)

enum class TabIcon(val menuIndex: Int) {
    HomeView(0),
    TodoView(1),
    CalendarView(2),
    LinksView(3),
    ProfileView(4),
    AdminView(5)
}

fun sideBarItems(isAdmin: Boolean): List<SideBarModel> {
    val items = mutableListOf(
        SideBarModel(Icons.Default.Home, "Home", TabIcon.HomeView, 0),
        SideBarModel(Icons.Default.List, "To-Do List", TabIcon.TodoView, 1),
        SideBarModel(Icons.Default.CalendarToday, "Calendar", TabIcon.CalendarView, 2),
        SideBarModel(Icons.Default.Link, "Links", TabIcon.LinksView, 3),
        SideBarModel(Icons.Default.Person, "Profile", TabIcon.ProfileView, 4)
    )

    if (isAdmin) {
        items.add(
            SideBarModel(
                Icons.Default.AdminPanelSettings,
                "Admin",
                TabIcon.AdminView,
                5
            )
        )
    }

    return items
}

enum class DeleteState {
    IDLE,
    CONFIRM_START,
    COUNTING_DOWN,
    READY_TO_DELETE
}