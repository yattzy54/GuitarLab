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
            "guitarlab_db",
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideFavoriteTuningDao(db: GuitarLabDatabase): FavoriteTuningDao = db.favoriteTuningDao()

    @Provides
    fun providePracticeSessionDao(db: GuitarLabDatabase): PracticeSessionDao = db.practiceSessionDao()

    @Provides
    fun provideRiffRecordDao(db: GuitarLabDatabase): RiffRecordDao = db.riffRecordDao()

    @Provides
    fun provideTabProjectDao(db: GuitarLabDatabase): TabProjectDao = db.tabProjectDao()
}
