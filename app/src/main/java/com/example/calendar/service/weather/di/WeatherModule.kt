package com.example.calendar.service.weather.di

import com.example.calendar.BuildConfig
import com.example.calendar.core.database.AppDatabase
import com.example.calendar.service.weather.data.location.LocationClientImpl
import com.example.calendar.service.weather.data.remote.WeatherApi
import com.example.calendar.service.weather.data.repository.WeatherRepositoryImpl
import com.example.calendar.service.weather.domain.repository.LocationClient
import com.example.calendar.service.weather.domain.repository.WeatherRepository
import com.example.calendar.service.weather.domain.usecase.RefreshWeatherUseCase
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val weatherModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
    single { get<AppDatabase>().weatherDao() }
    single<WeatherRepository> { WeatherRepositoryImpl(get(), get(), BuildConfig.WEATHER_API_KEY) }
    single<LocationClient> { LocationClientImpl(get()) }
    factory { RefreshWeatherUseCase(get(), get()) }
}
