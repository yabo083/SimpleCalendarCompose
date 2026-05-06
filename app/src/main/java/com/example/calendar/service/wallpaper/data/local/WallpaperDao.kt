package com.example.calendar.service.wallpaper.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface WallpaperDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<WallpaperEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignmentEntity: DailyAssignmentEntity)

    @Query("SELECT * FROM daily_assignments WHERE assignedDate = :date LIMIT 1")
    suspend fun getAssignmentByDate(date: String): DailyAssignmentEntity?

    @Query("SELECT * FROM wallpaper_images WHERE id = :id")
    suspend fun getImageById(id: String): WallpaperEntity?

    @Query("SELECT * FROM wallpaper_images WHERE id NOT IN (SELECT image_id FROM daily_assignments)")
    suspend fun getUnusedImages(): List<WallpaperEntity>

    @Query("SELECT * FROM category_preferences WHERE category = :category")
    suspend fun getPreference(category: String): CategoryPreferenceEntity?

    @Transaction
    suspend fun upsertPreference(category: String, action: String){
        val pref = getPreference(category) ?: CategoryPreferenceEntity(category)
        val newPref = when(action) {
            "like" -> pref.copy(
                likeCount = pref.likeCount + 1,
                effectiveWeight = pref.effectiveWeight * 1.2f
            )
            "dislike" -> pref.copy(
                dislikeCount = pref.dislikeCount + 1,
                effectiveWeight = pref.effectiveWeight * 0.8f
            )
            else -> pref
        }
        insertPreference(newPref)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreference(preference: CategoryPreferenceEntity)

    @Query("SELECT localPath FROM wallpaper_images WHERE expiresAt < :expiresBefore")
    suspend fun getExpiredPaths(expiresBefore: Long): List<String>

    @Query("DELETE FROM daily_assignments WHERE image_id IN (SELECT id FROM wallpaper_images WHERE expiresAt < :expiresBefore)")
    suspend fun deleteAssignmentsForExpiredImages(expiresBefore: Long)

    @Query("DELETE FROM wallpaper_images WHERE expiresAt < :expiresBefore")
    suspend fun deleteExpired(expiresBefore: Long)
}
