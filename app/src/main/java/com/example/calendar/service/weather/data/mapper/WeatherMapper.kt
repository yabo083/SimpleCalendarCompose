package com.example.calendar.service.weather.data.mapper

import com.example.calendar.service.weather.data.local.WeatherEntity
import com.example.calendar.service.weather.data.remote.ForecastDay

object WeatherMapper {
    fun fromForecastDay(day: ForecastDay, updatedAt: Long): WeatherEntity = WeatherEntity(
        date = day.date,
        weatherCode = day.day.condition.code,
        weatherText = day.day.condition.text,
        tempMin = day.day.mintemp_c,
        tempMax = day.day.maxtemp_c,
        updatedAt = updatedAt
    )
}
