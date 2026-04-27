package com.example.calendar.feature.weather.di

import com.example.calendar.BuildConfig
import com.example.calendar.core.database.AppDatabase
import com.example.calendar.feature.weather.data.location.LocationClientImpl
import com.example.calendar.feature.weather.data.repository.WeatherRepositoryImpl
import com.example.calendar.feature.weather.domain.repository.LocationClient
import com.example.calendar.feature.weather.domain.repository.WeatherRepository
import com.example.calendar.feature.weather.domain.usecase.RefreshWeatherUseCase
import org.koin.dsl.module

val weatherModule = module {
    single { get<AppDatabase>().weatherDao() }
    single<WeatherRepository> { WeatherRepositoryImpl(get(), get(), BuildConfig.WEATHER_API_KEY) }
    single<LocationClient> { LocationClientImpl(get()) }
    factory { RefreshWeatherUseCase(get(), get()) }
}
