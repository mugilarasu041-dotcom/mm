package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackerDao {
    @Query("SELECT * FROM monthly_trackers WHERE yearMonth = :yearMonth LIMIT 1")
    fun getTrackerByYearMonth(yearMonth: String): Flow<TrackerEntity?>

    @Query("SELECT * FROM monthly_trackers WHERE yearMonth = :yearMonth LIMIT 1")
    suspend fun getTrackerByYearMonthOnce(yearMonth: String): TrackerEntity?

    @Query("SELECT * FROM monthly_trackers ORDER BY year DESC, month DESC")
    fun getAllTrackers(): Flow<List<TrackerEntity>>

    @Query("SELECT * FROM monthly_trackers WHERE year = :year ORDER BY month ASC")
    fun getTrackersForYear(year: Int): Flow<List<TrackerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTracker(entity: TrackerEntity)

    @Query("DELETE FROM monthly_trackers WHERE yearMonth = :yearMonth")
    suspend fun deleteTrackerByYearMonth(yearMonth: String)

    @Query("DELETE FROM monthly_trackers")
    suspend fun clearAll()
}
