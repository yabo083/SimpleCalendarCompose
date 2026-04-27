package com.example.calendar.feature.settings.di

import com.example.calendar.feature.settings.data.repository.SettingsRepositoryImpl
import com.example.calendar.feature.settings.domain.repository.SettingsRepository
import com.example.calendar.feature.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    viewModel { SettingsViewModel(get()) }
}
