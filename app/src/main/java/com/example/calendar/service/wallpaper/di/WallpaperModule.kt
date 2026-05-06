package com.example.calendar.service.wallpaper.di

import com.example.calendar.core.database.AppDatabase
import com.example.calendar.service.wallpaper.data.repository.UapiProWallpaperRepository
import com.example.calendar.service.wallpaper.domain.repository.WallpaperRepository
import com.example.calendar.service.wallpaper.domain.usecase.WallpaperUseCase
import org.koin.dsl.module

val wallpaperModule = module {
    single { get<AppDatabase>().wallpaperDao() }
    single<WallpaperRepository> { UapiProWallpaperRepository(get(), get()) }
    factory { WallpaperUseCase(get(), get()) }
}
