package com.example.calendar.app.navigation

enum class AppDestination(
    val route: String,
    val title: String,
    val shortLabel: String
) {
    Calendar(route = "calendar", title = "Calendar", shortLabel = "C"),
    Todo(route = "todo", title = "Todo", shortLabel = "T"),
    Settings(route = "settings", title = "Settings", shortLabel = "S");

    companion object {
        val bottomNavItems: List<AppDestination> = listOf(Calendar, Todo, Settings)
    }
}
