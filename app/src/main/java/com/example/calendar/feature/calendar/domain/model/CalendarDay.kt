package com.example.calendar.feature.calendar.domain.model

import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate,
    val isToday: Boolean,
    val isSelected: Boolean,
    val isCurrentMonth: Boolean,
    val hasEvents: Boolean = false,
    val weatherCode: Int? = null
)
