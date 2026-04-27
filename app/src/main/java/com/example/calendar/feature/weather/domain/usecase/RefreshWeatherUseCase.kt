package com.example.calendar.feature.weather.domain.usecase

import com.example.calendar.feature.weather.domain.repository.LocationClient
import com.example.calendar.feature.weather.domain.repository.WeatherRepository
import timber.log.Timber

class RefreshWeatherUseCase(
    private val weatherRepository: WeatherRepository,
    private val locationClient: LocationClient
) {
    suspend operator fun invoke(): Result<Unit> {
        val (lat, lon) = locationClient.getLatLng() ?: run {
            Timber.w("RefreshWeatherUseCase: no location available")
            return Result.failure(IllegalStateException("Location unavailable"))
        }
        Timber.d("RefreshWeatherUseCase: refreshing weather for $lat, $lon")
        return weatherRepository.refreshWeather(lat, lon).map {}
    }
}
