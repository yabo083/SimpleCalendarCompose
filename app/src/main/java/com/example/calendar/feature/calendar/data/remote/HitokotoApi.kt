package com.example.calendar.feature.calendar.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface HitokotoApi {
    @GET("/")
    suspend fun getHitokoto(
        @Query("c") category: String = "f"
    ): HitokotoResponse
}
