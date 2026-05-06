package com.example.calendar.core.di

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
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
}
