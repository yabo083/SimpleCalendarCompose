package com.example.calendar.service.hitokoto.data.remote

data class HitokotoResponse(
    val hitokoto: String,
    val from: String?,
    val from_who: String?
)
