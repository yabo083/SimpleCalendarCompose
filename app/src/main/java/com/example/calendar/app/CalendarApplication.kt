package com.example.calendar.app

import android.app.Application
import com.example.calendar.BuildConfig
import com.example.calendar.core.di.databaseModule
import com.example.calendar.core.di.networkModule
import com.example.calendar.feature.calendar.di.calendarModule
import com.example.calendar.feature.settings.di.settingsModule
import com.example.calendar.service.hitokoto.di.hitokotoModule
import com.example.calendar.service.wallpaper.di.wallpaperModule
import com.example.calendar.service.weather.di.weatherModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class CalendarApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            androidContext(this@CalendarApplication)
            modules(
                networkModule,
                databaseModule,
                hitokotoModule,
                weatherModule,
                settingsModule,
                calendarModule,
                wallpaperModule
            )
        }
    }
}
