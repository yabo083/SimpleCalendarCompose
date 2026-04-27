package com.example.calendar.feature.calendar.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.calendar.app.navigation.AppDestination

fun NavGraphBuilder.calendarScreen() {
    composable(route = AppDestination.Calendar.route) {
        CalendarScreen()
    }
}
