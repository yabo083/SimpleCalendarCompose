package com.example.calendar.service.wallpaper.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_preferences")
data class CategoryPreferenceEntity(
    @PrimaryKey val category: String,
    val baseWeight: Int = 100,
    val likeCount: Int = 0,
    val dislikeCount: Int = 0,
    val effectiveWeight: Float = 1.0f
)
