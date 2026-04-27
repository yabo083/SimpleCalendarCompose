package com.example.calendar.feature.settings.ui

import androidx.lifecycle.ViewModel
import com.example.calendar.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _todayRefreshMinutes = MutableStateFlow(settingsRepository.getTodayRefreshMinutes())
    val todayRefreshMinutes: StateFlow<Long> = _todayRefreshMinutes.asStateFlow()

    private val _futureRefreshHours = MutableStateFlow(settingsRepository.getFutureRefreshHours())
    val futureRefreshHours: StateFlow<Long> = _futureRefreshHours.asStateFlow()

    fun setTodayRefreshMinutes(minutes: Long) {
        _todayRefreshMinutes.value = minutes
        settingsRepository.setTodayRefreshMinutes(minutes)
    }

    fun setFutureRefreshHours(hours: Long) {
        _futureRefreshHours.value = hours
        settingsRepository.setFutureRefreshHours(hours)
    }
}
