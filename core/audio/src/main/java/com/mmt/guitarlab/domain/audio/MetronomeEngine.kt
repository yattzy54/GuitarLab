package com.mmt.guitarlab.domain.audio

import com.mmt.guitarlab.domain.model.MetronomeBeat
import com.mmt.guitarlab.domain.model.MetronomeConfig
import kotlinx.coroutines.flow.StateFlow

interface MetronomeEngine {
    val config: StateFlow<MetronomeConfig>
    val beat: StateFlow<MetronomeBeat?>
    val isRunning: StateFlow<Boolean>
    fun updateConfig(transform: (MetronomeConfig) -> MetronomeConfig)
    fun start()
    fun stop()
}
