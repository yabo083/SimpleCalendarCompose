package com.example.calendar.service.hitokoto.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface HitokotoApi {
    @GET("/")
    suspend fun getHitokoto(
        @Query("c") category: String = "f"
    ): HitokotoResponse
}
