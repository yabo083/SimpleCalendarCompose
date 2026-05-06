package com.example.calendar.service.hitokoto.di

import com.example.calendar.service.hitokoto.data.remote.HitokotoApi
import com.example.calendar.service.hitokoto.domain.usecase.LoadQuoteUseCase
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val hitokotoModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://v1.hitokoto.cn/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HitokotoApi::class.java)
    }
    factory { LoadQuoteUseCase(get()) }
}
