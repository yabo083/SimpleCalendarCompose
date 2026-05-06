package com.example.calendar.service.wallpaper.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpaper_images")
data class WallpaperEntity(
    @PrimaryKey val id: String,
    val sourceUrl: String,
    val localPath: String,
    val category: String,
    val type: String?,
    val width: Int?,
    val height: Int?,
    val fileSize: Long?,
    val fetchedAt: Long,
    val expiresAt: Long
)
