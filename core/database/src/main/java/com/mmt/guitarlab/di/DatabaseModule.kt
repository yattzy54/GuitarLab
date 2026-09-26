package com.mmt.guitarlab.di

import android.content.Context
import androidx.room.Room
import com.mmt.guitarlab.data.db.FavoriteTuningDao
import com.mmt.guitarlab.data.db.GuitarLabDatabase
import com.mmt.guitarlab.data.db.PracticeSessionDao
import com.mmt.guitarlab.data.db.RiffRecordDao
import com.mmt.guitarlab.data.db.TabProjectDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GuitarLabDatabase {
        return Room.databaseBuilder(
            context,
            GuitarLabDatabase::class.java,
            "guitar_lab_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideTabProjectDao(database: GuitarLabDatabase): TabProjectDao {
        return database.tabProjectDao()
    }

    @Provides
    fun providePracticeSessionDao(database: GuitarLabDatabase): PracticeSessionDao {
        return database.practiceSessionDao()
    }

    @Provides
    fun provideRiffRecordDao(database: GuitarLabDatabase): RiffRecordDao {
        return database.riffRecordDao()
    }

    @Provides
    fun provideFavoriteTuningDao(database: GuitarLabDatabase): FavoriteTuningDao {
        return database.favoriteTuningDao()
    }
}
