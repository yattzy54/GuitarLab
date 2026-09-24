package com.mmt.guitarlab.domain.audio

import com.mmt.guitarlab.domain.model.DrumKit
import com.mmt.guitarlab.domain.model.DrumPattern
import com.mmt.guitarlab.domain.model.DrumSound
import kotlinx.coroutines.flow.StateFlow

interface DrumEngine {
    val isPlaying: StateFlow<Boolean>
    val currentStep: StateFlow<Int>
    val bpm: StateFlow<Int>
    val volume: StateFlow<Float>
    val swing: StateFlow<Float>
    val pattern: StateFlow<DrumPattern>
    val drumKit: StateFlow<DrumKit>

    fun start()
    fun stop()
    fun setBpm(bpm: Int)
    fun setVolume(volume: Float)
    fun setSwing(swing: Float)
    fun setPattern(pattern: DrumPattern)
    fun setDrumKit(kit: DrumKit)
    fun toggleStep(sound: DrumSound, step: Int)
    fun previewSound(sound: DrumSound)
}
