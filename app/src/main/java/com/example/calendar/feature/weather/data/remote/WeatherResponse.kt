package com.example.calendar.feature.weather.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class ForecastResponse(
    val forecast: Forecast
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: DayInfo
)

data class DayInfo(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val condition: Condition
)

data class Condition(
    val code: Int,
    val text: String = ""
)

interface WeatherApi {
    @GET("v1/forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 14
    ): ForecastResponse
}
