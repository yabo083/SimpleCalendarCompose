package com.example.calendar.feature.settings.data.repository

import android.content.Context
import com.example.calendar.feature.settings.domain.repository.SettingsRepository
import androidx.core.content.edit

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    private val prefs = context.getSharedPreferences("calendar_settings", Context.MODE_PRIVATE)

    override fun getTodayRefreshMinutes(): Long = prefs.getLong("today_refresh_minutes", 60)
    override fun getFutureRefreshHours(): Long = prefs.getLong("future_refresh_hours", 24)
    override fun setTodayRefreshMinutes(minutes: Long) {
        prefs.edit { putLong("today_refresh_minutes", minutes) }
    }

    override fun setFutureRefreshHours(hours: Long) {
        prefs.edit { putLong("future_refresh_hours", hours) }
    }
}
