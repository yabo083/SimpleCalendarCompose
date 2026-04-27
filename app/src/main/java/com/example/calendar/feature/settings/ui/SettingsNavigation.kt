package com.example.calendar.feature.settings.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.calendar.app.navigation.AppDestination

fun NavGraphBuilder.settingsScreen() {
    composable(route = AppDestination.Settings.route) {
        SettingsScreen()
    }
}
