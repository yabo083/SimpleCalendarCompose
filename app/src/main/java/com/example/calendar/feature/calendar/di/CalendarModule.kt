package com.example.calendar.feature.calendar.di

import com.example.calendar.feature.calendar.domain.usecase.GetMonthDaysUseCase
import com.example.calendar.feature.calendar.domain.usecase.LoadQuoteUseCase
import com.example.calendar.feature.calendar.ui.CalendarViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val calendarModule = module {
    factory { GetMonthDaysUseCase() }
    factory { LoadQuoteUseCase(get()) }
    viewModel { CalendarViewModel(get(), get(), get(), get(), get()) }
}
