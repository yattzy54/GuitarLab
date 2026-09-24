package com.mmt.guitarlab.di

import com.mmt.guitarlab.audio.drums.AudioTrackDrumEngine
import com.mmt.guitarlab.audio.metronome.AudioTrackMetronomeEngine
import com.mmt.guitarlab.audio.tuner.AudioRecordTunerEngine
import com.mmt.guitarlab.data.SettingsRepositoryImpl
import com.mmt.guitarlab.domain.audio.DrumEngine
import com.mmt.guitarlab.domain.audio.MetronomeEngine
import com.mmt.guitarlab.domain.audio.TunerEngine
import com.mmt.guitarlab.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindTunerEngine(impl: AudioRecordTunerEngine): TunerEngine

    @Binds
    @Singleton
    abstract fun bindMetronomeEngine(impl: AudioTrackMetronomeEngine): MetronomeEngine

    @Binds
    @Singleton
    abstract fun bindDrumEngine(impl: AudioTrackDrumEngine): DrumEngine

    @Binds
    @Singleton
    abstract fun bindSettings(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindTuningRepository(impl: com.mmt.guitarlab.data.repository.TuningRepositoryImpl): com.mmt.guitarlab.domain.repository.TuningRepository

    @Binds
    @Singleton
    abstract fun bindTabRepository(impl: com.mmt.guitarlab.data.repository.TabRepositoryImpl): com.mmt.guitarlab.domain.repository.TabRepository
}
