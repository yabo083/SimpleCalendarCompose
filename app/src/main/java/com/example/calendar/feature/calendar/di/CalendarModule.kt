package com.example.calendar.feature.calendar.di

import com.example.calendar.feature.calendar.domain.usecase.GetMonthDaysUseCase
import com.example.calendar.feature.calendar.ui.CalendarViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val calendarModule = module {
    factory { GetMonthDaysUseCase() }
    viewModel { CalendarViewModel(get(), get(), get(), get(), get(), get()) }
}
