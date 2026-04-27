package com.example.calendar.feature.weather.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<WeatherEntity>)

    @Query("DELETE FROM weather_daily")
    suspend fun deleteAll()

    @Query("SELECT * FROM weather_daily WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): WeatherEntity?

    @Query("SELECT * FROM weather_daily")
    fun getAll(): Flow<List<WeatherEntity>>
}
