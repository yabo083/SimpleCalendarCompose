package com.example.calendar.feature.weather.domain.repository

import com.example.calendar.feature.weather.data.local.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeatherFlow(): Flow<List<WeatherEntity>>
    suspend fun refreshWeather(lat: Double, lon: Double): Result<List<WeatherEntity>>
}
