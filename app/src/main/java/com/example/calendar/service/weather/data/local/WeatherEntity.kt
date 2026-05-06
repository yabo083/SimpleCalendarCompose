package com.example.calendar.service.weather.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_daily")
data class WeatherEntity(
    @PrimaryKey
    val date: String,
    val weatherCode: Int,
    val weatherText: String,
    val tempMin: Double,
    val tempMax: Double,
    val updatedAt: Long
)
