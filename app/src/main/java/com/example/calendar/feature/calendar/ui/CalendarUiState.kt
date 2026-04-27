package com.example.calendar.feature.calendar.ui

import com.example.calendar.feature.calendar.domain.model.CalendarDay
import java.time.LocalDate

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: LocalDate = LocalDate.now().withDayOfMonth(1),
    val monthDaysMap: Map<LocalDate, List<CalendarDay>> = emptyMap(),
    val isLoading: Boolean = false,
    val quoteText: String = "",
    val quoteAuthor: String? = null,
    val isQuoteLoading: Boolean = false
)
