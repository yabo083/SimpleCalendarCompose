package com.example.calendar.service.wallpaper.data.repository

import android.content.Context
import android.icu.util.Calendar
import com.example.calendar.BuildConfig
import com.example.calendar.service.wallpaper.data.local.DailyAssignmentEntity
import com.example.calendar.service.wallpaper.data.local.WallpaperDao
import com.example.calendar.service.wallpaper.data.local.WallpaperEntity
import com.example.calendar.service.wallpaper.domain.policy.WallpaperRequestSelector
import com.example.calendar.service.wallpaper.domain.repository.WallpaperRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.UUID

class UapiProWallpaperRepository(
    private val dao: WallpaperDao,
    private val context: Context
) : WallpaperRepository {

    private val client = OkHttpClient()

    override suspend fun fetchWeekBatch(deviceWidthDp: Int) {
        val calendar = Calendar.getInstance()
        for (i in 0 until 7) {
            val dateStr = String.format("%tF", calendar.time)
            val imageRequest = WallpaperRequestSelector.select(deviceWidthDp, i)
            val entity = fetchAndSaveImage(imageRequest.category, imageRequest.type)
            if (entity != null) {
                dao.insertAssignment(
                    DailyAssignmentEntity(
                        id = UUID.randomUUID().toString(),
                        imageId = entity.id,
                        assignedDate = dateStr
                    )
                )
            }
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }
    }

    override suspend fun assignImageToDate(imageId: String, date: String) {
        dao.insertAssignment(
            DailyAssignmentEntity(
                id = UUID.randomUUID().toString(),
                imageId = imageId,
                assignedDate = date
            )
        )
    }

    override suspend fun getTodayWallpaper(date: String): WallpaperEntity? {
        val assignment = dao.getAssignmentByDate(date) ?: return null
        return dao.getImageById(assignment.imageId)
    }

    override suspend fun getUnusedImages(): List<WallpaperEntity> = dao.getUnusedImages()

    override suspend fun fetchSingleImage(category: String, type: String?): WallpaperEntity? {
        return fetchAndSaveImage(category, type)
    }

    override suspend fun recordPreference(category: String, action: String) {
        dao.upsertPreference(category, action)
    }

    override suspend fun cleanupExpired(expiresBefore: Long) {
        val paths = dao.getExpiredPaths(expiresBefore)
        paths.forEach { path ->
            File(context.filesDir, path).delete()
        }
        dao.deleteAssignmentsForExpiredImages(expiresBefore)
        dao.deleteExpired(expiresBefore)
    }

    private suspend fun fetchAndSaveImage(category: String, type: String?): WallpaperEntity? {
        val url = "https://uapis.cn/api/v1/random/image".toHttpUrl().newBuilder()
            .addQueryParameter("category", category)
            .apply {
                if (type != null) {
                    addQueryParameter("type", type)
                }
            }
            .build()

        val requestBuilder = Request.Builder().url(url)
        if (BuildConfig.UAPI_API_KEY.isNotBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer ${BuildConfig.UAPI_API_KEY}")
        }

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(requestBuilder.build()).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null
                    val body = response.body ?: return@withContext null

                    val uuid = UUID.randomUUID().toString()
                    val relativePath = "wallpapers/$uuid.${extensionFor(response.header("Content-Type"))}"
                    val file = File(context.filesDir, relativePath)
                    file.parentFile?.mkdirs()

                    file.outputStream().use { body.byteStream().copyTo(it) }

                    val entity = WallpaperEntity(
                        id = uuid,
                        sourceUrl = response.request.url.toString(),
                        localPath = relativePath,
                        category = category,
                        type = type,
                        width = null,
                        height = null,
                        fileSize = file.length(),
                        fetchedAt = System.currentTimeMillis(),
                        expiresAt = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000
                    )

                    dao.insertImages(listOf(entity))
                    entity
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun extensionFor(contentType: String?): String {
        return when {
            contentType?.contains("webp", ignoreCase = true) == true -> "webp"
            contentType?.contains("png", ignoreCase = true) == true -> "png"
            else -> "jpg"
        }
    }
}
