package com.mmt.guitarlab.ui.metronome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmt.guitarlab.data.MetronomeConfigCodec
import com.mmt.guitarlab.domain.audio.MetronomeEngine
import com.mmt.guitarlab.domain.model.MetronomeBeat
import com.mmt.guitarlab.domain.model.MetronomeConfig
import com.mmt.guitarlab.domain.model.MetronomeSound
import com.mmt.guitarlab.domain.model.TimeSignature
import com.mmt.guitarlab.domain.model.TrainerIntervalKind
import com.mmt.guitarlab.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetronomeViewModel @Inject constructor(
    private val engine: MetronomeEngine,
    private val settings: SettingsRepository,
) : ViewModel() {

    val config: StateFlow<MetronomeConfig> = engine.config
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MetronomeConfig())

    val beat: StateFlow<MetronomeBeat?> = engine.beat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val running: StateFlow<Boolean> = engine.isRunning
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val tapTimestamps = mutableListOf<Long>()

    init {
        viewModelScope.launch {
            val saved = MetronomeConfigCodec.decode(settings.metronomeJson.first())
            engine.updateConfig { saved }
            engine.config.collect { settings.setMetronomeJson(MetronomeConfigCodec.encode(it)) }
        }
    }

    fun setBpm(bpm: Int) = engine.updateConfig { it.copy(bpm = bpm.coerceIn(MetronomeConfig.MIN_BPM, MetronomeConfig.MAX_BPM)) }

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
                val bpm = (60_000.0 / avgInterval).toInt().coerceIn(MetronomeConfig.MIN_BPM, MetronomeConfig.MAX_BPM)
                setBpm(bpm)
            }
        }
    }

    fun setTimeSignature(ts: TimeSignature) = engine.updateConfig { it.copy(timeSignature = ts) }

    fun setVolume(volume: Float) = engine.updateConfig { it.copy(volume = volume) }

    fun setTrainerEnabled(enabled: Boolean) {
        engine.updateConfig { cfg ->
            val trainer = cfg.trainer.copy(enabled = enabled)
            cfg.copy(
                trainer = trainer,
                bpm = if (enabled) trainer.startBpm else cfg.bpm,
            )
        }
    }

    fun setTrainerStart(bpm: Int) = engine.updateConfig {
        it.copy(trainer = it.trainer.copy(startBpm = bpm))
    }

    fun setTrainerTarget(bpm: Int) = engine.updateConfig {
        it.copy(trainer = it.trainer.copy(targetBpm = bpm))
    }

    fun setTrainerIncrement(step: Int) = engine.updateConfig {
        it.copy(trainer = it.trainer.copy(incrementBpm = step.coerceIn(1, 20)))
    }

    fun setTrainerIntervalKind(kind: TrainerIntervalKind) = engine.updateConfig {
        it.copy(trainer = it.trainer.copy(intervalKind = kind))
    }

    fun setTrainerIntervalValue(value: Int) = engine.updateConfig {
        it.copy(trainer = it.trainer.copy(intervalValue = value.coerceIn(1, 60)))
    }

    fun setSound(sound: MetronomeSound) = engine.updateConfig { it.copy(sound = sound) }

    fun setVibrateOnly(vibrate: Boolean) = engine.updateConfig { it.copy(vibrateOnly = vibrate) }

    fun toggle() {
        if (engine.isRunning.value) engine.stop() else engine.start()
    }

    override fun onCleared() {
        engine.stop()
        super.onCleared()
    }
}
