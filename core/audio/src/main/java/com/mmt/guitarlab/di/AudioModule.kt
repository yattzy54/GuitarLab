package com.mmt.guitarlab.di

import com.mmt.guitarlab.audio.drums.AudioTrackDrumEngine
import com.mmt.guitarlab.audio.metronome.AudioTrackMetronomeEngine
import com.mmt.guitarlab.audio.tuner.AudioRecordTunerEngine
import com.mmt.guitarlab.domain.audio.DrumEngine
import com.mmt.guitarlab.domain.audio.MetronomeEngine
import com.mmt.guitarlab.domain.audio.TunerEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AudioModule {

    @Binds
    @Singleton
    abstract fun bindDrumEngine(impl: AudioTrackDrumEngine): DrumEngine

    @Binds
    @Singleton
    abstract fun bindMetronomeEngine(impl: AudioTrackMetronomeEngine): MetronomeEngine

    @Binds
    @Singleton
    abstract fun bindTunerEngine(impl: AudioRecordTunerEngine): TunerEngine
}
