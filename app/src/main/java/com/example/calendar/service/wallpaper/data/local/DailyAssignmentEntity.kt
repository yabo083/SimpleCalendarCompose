package com.example.calendar.service.wallpaper.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_assignments",
    indices = [
        Index(value = ["assignedDate"], unique = true),
        Index(value = ["image_id"])
    ]
)
data class DailyAssignmentEntity(
    @PrimaryKey val id: String,
    @ColumnInfo("image_id") val imageId: String,
    val assignedDate: String,
    val isUsed: Boolean = false
)
