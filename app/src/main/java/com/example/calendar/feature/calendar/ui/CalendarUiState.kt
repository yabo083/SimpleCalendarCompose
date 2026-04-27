package com.example.calendar.feature.calendar.ui

import java.time.LocalDate

data class CalenderUiState(
    val selecetedDate: LocalDate = LocalDate.now(),
    val currentMonth: LocalDate = LocalDate.now().withDayOfMonth(1),
    val isLoading: Boolean = false,
    val events: List<String> = emptyList()
)