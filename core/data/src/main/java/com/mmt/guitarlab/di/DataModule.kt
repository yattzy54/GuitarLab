package com.mmt.guitarlab.di

import com.mmt.guitarlab.data.SettingsRepositoryImpl
import com.mmt.guitarlab.data.repository.TabRepositoryImpl
import com.mmt.guitarlab.data.repository.TuningRepositoryImpl
import com.mmt.guitarlab.domain.repository.SettingsRepository
import com.mmt.guitarlab.domain.repository.TabRepository
import com.mmt.guitarlab.domain.repository.TuningRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindTabRepository(impl: TabRepositoryImpl): TabRepository

    @Binds
    @Singleton
    abstract fun bindTuningRepository(impl: TuningRepositoryImpl): TuningRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
