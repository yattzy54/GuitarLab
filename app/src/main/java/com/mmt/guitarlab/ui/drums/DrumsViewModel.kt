package com.mmt.guitarlab.ui.drums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmt.guitarlab.domain.audio.DrumEngine
import com.mmt.guitarlab.domain.model.DrumPattern
import com.mmt.guitarlab.domain.model.DrumSound
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DrumsViewModel @Inject constructor(
    private val engine: DrumEngine,
) : ViewModel() {

    val isPlaying: StateFlow<Boolean> = engine.isPlaying
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val currentStep: StateFlow<Int> = engine.currentStep
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), -1)

    val bpm: StateFlow<Int> = engine.bpm
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 110)

    val volume: StateFlow<Float> = engine.volume
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.85f)

    val swing: StateFlow<Float> = engine.swing
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0f)

    val pattern: StateFlow<DrumPattern> = engine.pattern
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DrumPattern.DEFAULT_PATTERNS.first())

    val drumKit: StateFlow<com.mmt.guitarlab.domain.model.DrumKit> = engine.drumKit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), com.mmt.guitarlab.domain.model.DrumKit.ROCK)

    val availablePatterns: List<DrumPattern> = DrumPattern.DEFAULT_PATTERNS

    private val tapTimestamps = mutableListOf<Long>()

    fun togglePlay() {
        if (isPlaying.value) {
            engine.stop()
        } else {
            engine.start()
        }
    }

    fun setBpm(newBpm: Int) {
        engine.setBpm(newBpm)
    }

    fun adjustBpm(delta: Int) {
        setBpm(bpm.value + delta)
    }

    fun setVolume(vol: Float) {
        engine.setVolume(vol)
    }

    fun setSwing(sw: Float) {
        engine.setSwing(sw)
    }

    fun selectPattern(pattern: DrumPattern) {
        engine.setPattern(pattern)
    }

    fun setDrumKit(kit: com.mmt.guitarlab.domain.model.DrumKit) {
        engine.setDrumKit(kit)
    }

    fun toggleStep(sound: DrumSound, step: Int) {
        engine.toggleStep(sound, step)
    }

    fun previewSound(sound: DrumSound) {
        engine.previewSound(sound)
    }

    fun onTapTempo() {
        val now = System.currentTimeMillis()
        if (tapTimestamps.isNotEmpty() && now - tapTimestamps.last() > 2500) {
            tapTimestamps.clear()
        }
        tapTimestamps.add(now)
        if (tapTimestamps.size > 5) {
            tapTimestamps.removeAt(0)
        }
        if (tapTimestamps.size >= 2) {
            val intervals = tapTimestamps.zipWithNext { a, b -> b - a }
            val avgInterval = intervals.average()
            if (avgInterval > 0) {
                val calculatedBpm = (60_000.0 / avgInterval).toInt().coerceIn(30, 300)
                setBpm(calculatedBpm)
            }
        }
    }

    override fun onCleared() {
        engine.stop()
        super.onCleared()
    }
}
