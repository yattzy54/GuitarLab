package com.mmt.guitarlab.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTuningDao {
    @Query("SELECT tuningId FROM favorite_tunings")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteTuningEntity)

    @Query("DELETE FROM favorite_tunings WHERE tuningId = :tuningId")
    suspend fun deleteFavorite(tuningId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tunings WHERE tuningId = :tuningId)")
    suspend fun isFavorite(tuningId: String): Boolean
}

@Dao
interface PracticeSessionDao {
    @Query("SELECT * FROM practice_sessions ORDER BY dateMillis DESC")
    fun getAllSessions(): Flow<List<PracticeSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PracticeSessionEntity)

    @Delete
    suspend fun deleteSession(session: PracticeSessionEntity)
}

@Dao
interface RiffRecordDao {
    @Query("SELECT * FROM riff_records ORDER BY timestamp DESC")
    fun getAllRiffs(): Flow<List<RiffRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiff(riff: RiffRecordEntity)

    @Delete
    suspend fun deleteRiff(riff: RiffRecordEntity)
}
