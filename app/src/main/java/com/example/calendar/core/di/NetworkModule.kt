package com.example.calendar.core.di

import com.example.calendar.BuildConfig
import com.example.calendar.feature.calendar.data.remote.HitokotoApi
import com.example.calendar.feature.weather.data.remote.WeatherApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

val networkModule = module {
    single {
        val loggingInterceptor = HttpLoggingInterceptor { msg ->
            Timber.tag("OkHttp").d(msg)
        }.apply { level = HttpLoggingInterceptor.Level.BODY }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://v1.hitokoto.cn/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HitokotoApi::class.java)
    }
}
