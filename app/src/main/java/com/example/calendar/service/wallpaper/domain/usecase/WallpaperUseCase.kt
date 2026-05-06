package com.example.calendar.service.wallpaper.domain.usecase

import android.content.Context
import com.example.calendar.service.wallpaper.domain.repository.WallpaperRepository
import java.io.File
import java.time.LocalDate

class WallpaperUseCase(
    private val repository: WallpaperRepository,
    private val context: Context
) {
    data class WallpaperInfo(val path: String, val category: String)

    suspend fun getImageForToday(deviceWidthDp: Int): WallpaperInfo? {
        repository.cleanupExpired(System.currentTimeMillis())
        val today = LocalDate.now().toString()
        val todayWallpaper = repository.getTodayWallpaper(today)

        val entity = if (todayWallpaper != null) {
            todayWallpaper
        } else {
            val unused = repository.getUnusedImages()
            if (unused.isNotEmpty()) {
                val selected = unused.random()
                repository.assignImageToDate(selected.id, today)
                selected
            } else {
                repository.fetchWeekBatch(deviceWidthDp)
                repository.getTodayWallpaper(today)
            }
        }

        return entity?.let {
            WallpaperInfo(
                path = File(context.filesDir, it.localPath).absolutePath,
                category = it.category
            )
        }
    }

    suspend fun recordLike(category: String) {
        repository.recordPreference(category, "like")
    }

    suspend fun recordDislikeAndSwap(category: String): WallpaperInfo? {
        repository.recordPreference(category, "dislike")
        val today = LocalDate.now().toString()
        val entity = repository.fetchSingleImage(category, null)?.also {
            repository.assignImageToDate(it.id, today)
        } ?: repository.getUnusedImages().randomOrNull()?.also { selected ->
            val today = LocalDate.now().toString()
            repository.assignImageToDate(selected.id, today)
        }

        return entity?.let {
            WallpaperInfo(
                path = File(context.filesDir, it.localPath).absolutePath,
                category = it.category
            )
        }
    }
}
