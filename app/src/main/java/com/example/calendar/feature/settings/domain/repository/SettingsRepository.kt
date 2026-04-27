package com.example.calendar.feature.settings.domain.repository

interface SettingsRepository {
    fun getTodayRefreshMinutes(): Long
    fun getFutureRefreshHours(): Long
    fun setTodayRefreshMinutes(minutes: Long)
    fun setFutureRefreshHours(hours: Long)
}
