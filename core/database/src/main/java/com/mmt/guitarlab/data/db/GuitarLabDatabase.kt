package com.mmt.guitarlab.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteTuningEntity::class,
        PracticeSessionEntity::class,
        RiffRecordEntity::class,
        TabProjectEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class GuitarLabDatabase : RoomDatabase() {
    abstract fun favoriteTuningDao(): FavoriteTuningDao
    abstract fun practiceSessionDao(): PracticeSessionDao
    abstract fun riffRecordDao(): RiffRecordDao
    abstract fun tabProjectDao(): TabProjectDao
}
