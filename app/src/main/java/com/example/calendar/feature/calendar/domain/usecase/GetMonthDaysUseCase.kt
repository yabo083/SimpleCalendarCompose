package com.example.calendar.feature.calendar.domain.usecase

import com.example.calendar.feature.calendar.domain.model.CalendarDay
import com.example.calendar.feature.weather.data.local.WeatherEntity
import java.time.LocalDate

class GetMonthDaysUseCase {
    operator fun invoke(
        currentMonth: LocalDate,
        selectedDate: LocalDate,
        weatherList: List<WeatherEntity>
    ): List<CalendarDay> {
        val firstDayOfWeek = currentMonth.dayOfWeek.value % 7
        val startDate = currentMonth.minusDays(firstDayOfWeek.toLong())
        val today = LocalDate.now()

        return (0 until 42).map { i ->
            val date = startDate.plusDays(i.toLong())
            val weather = weatherList.find { it.date == date.toString() }
            CalendarDay(
                date = date,
                isToday = date == today,
                isSelected = date == selectedDate,
                isCurrentMonth = date.month == currentMonth.month && date.year == currentMonth.year,
                weatherCode = weather?.weatherCode
            )
        }
    }
}
