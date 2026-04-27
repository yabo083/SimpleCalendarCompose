package com.example.calendar.feature.weather.data.repository

import com.example.calendar.feature.weather.data.local.WeatherDao
import com.example.calendar.feature.weather.data.local.WeatherEntity
import com.example.calendar.feature.weather.data.mapper.WeatherMapper
import com.example.calendar.feature.weather.data.remote.WeatherApi
import com.example.calendar.feature.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class WeatherRepositoryImpl(
    private val weatherApi: WeatherApi,
    private val weatherDao: WeatherDao,
    private val apiKey: String
) : WeatherRepository {
    override fun getWeatherFlow(): Flow<List<WeatherEntity>> = weatherDao.getAll()

    override suspend fun refreshWeather(lat: Double, lon: Double): Result<List<WeatherEntity>> {
        return try {
            val response = weatherApi.getForecast(apiKey, "$lat,$lon", 14)
            val now = System.currentTimeMillis()
            val entities = response.forecast.forecastday.map { WeatherMapper.fromForecastDay(it, now) }
            weatherDao.insertAll(entities)
            Timber.d("WeatherRepositoryImpl: refreshed ${entities.size} days")
            Result.success(entities)
        } catch (e: Exception) {
            Timber.e(e, "WeatherRepositoryImpl: refresh failed")
            Result.failure(e)
        }
    }
}
