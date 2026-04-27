package com.example.calendar.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.calendar.feature.calendar.ui.calendarScreen
import com.example.calendar.feature.settings.ui.settingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Calendar.route,
        modifier = modifier
    ) {
        calendarScreen()
        composable(route = AppDestination.Todo.route) {
            com.example.calendar.feature.todo.ui.TodoScreen()
        }
        settingsScreen()
    }
}
