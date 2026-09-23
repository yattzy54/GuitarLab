package com.mmt.guitarlab.domain.audio

import com.mmt.guitarlab.domain.model.DetectedPitch
import kotlinx.coroutines.flow.StateFlow

interface TunerEngine {
    val pitch: StateFlow<DetectedPitch?>
    val isRunning: StateFlow<Boolean>
    fun setA4(hz: Float)
    fun start()
    fun stop()
}
