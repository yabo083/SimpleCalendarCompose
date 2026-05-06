package com.example.calendar.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.calendar.service.wallpaper.data.local.CategoryPreferenceEntity
import com.example.calendar.service.wallpaper.data.local.DailyAssignmentEntity
import com.example.calendar.service.wallpaper.data.local.WallpaperDao
import com.example.calendar.service.wallpaper.data.local.WallpaperEntity
import com.example.calendar.service.weather.data.local.WeatherDao
import com.example.calendar.service.weather.data.local.WeatherEntity

@Database(
    entities = [WeatherEntity::class, WallpaperEntity::class, DailyAssignmentEntity::class, CategoryPreferenceEntity::class],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    abstract fun wallpaperDao(): WallpaperDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDataBase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "calendar_database"
                ).fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
