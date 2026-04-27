package com.example.calendar.core.di

import com.example.calendar.core.database.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.getDataBase(get()) }
}
