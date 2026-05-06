package com.example.calendar.service.wallpaper.domain.repository

import com.example.calendar.service.wallpaper.data.local.WallpaperEntity

interface WallpaperRepository {
    suspend fun fetchWeekBatch(deviceWidthDp: Int)
    suspend fun getTodayWallpaper(date: String): WallpaperEntity?
    suspend fun getUnusedImages(): List<WallpaperEntity>
    suspend fun fetchSingleImage(category: String, type: String?): WallpaperEntity?
    suspend fun recordPreference(category: String, action: String)
    suspend fun cleanupExpired(expiresBefore: Long)
    suspend fun assignImageToDate(imageId: String, date: String)
}
