package com.mmt.guitarlab.domain.repository

import kotlinx.coroutines.flow.Flow

data class TunerSettings(val a4Hz: Float = 440f)

interface SettingsRepository {
    val tunerSettings: Flow<TunerSettings>
    val metronomeJson: Flow<String?>
    suspend fun setA4(hz: Float)
    suspend fun setMetronomeJson(json: String)
}
