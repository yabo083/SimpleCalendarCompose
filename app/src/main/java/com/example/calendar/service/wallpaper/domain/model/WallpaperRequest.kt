package com.example.calendar.service.wallpaper.domain.model

data class WallpaperRequest(
    val category: String,
    val type: String? = null
)
