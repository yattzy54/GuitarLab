package com.mmt.guitarlab.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "practice_sessions")
data class PracticeSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long,
    val durationMinutes: Int,
    val notes: String,
    val category: String = "General",
)

@Entity(tableName = "favorite_tunings")
data class FavoriteTuningEntity(
    @PrimaryKey val tuningId: String,
)

@Entity(tableName = "riff_records")
data class RiffRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filePath: String,
    val title: String,
    val timestamp: Long,
    val durationMs: Long,
    val bpm: Int,
    val tuningName: String,
)
