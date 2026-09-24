package com.mmt.guitarlab.ui.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmt.guitarlab.domain.audio.TunerEngine
import com.mmt.guitarlab.domain.model.DetectedPitch
import com.mmt.guitarlab.domain.model.Tuning
import com.mmt.guitarlab.domain.model.TuningNote
import com.mmt.guitarlab.domain.repository.SettingsRepository
import com.mmt.guitarlab.domain.repository.TuningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.ln

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TunerViewModel @Inject constructor(
    private val tuner: TunerEngine,
    private val settings: SettingsRepository,
    private val tuningRepository: TuningRepository,
) : ViewModel() {

    val pitch: StateFlow<DetectedPitch?> = tuner.pitch
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val running: StateFlow<Boolean> = tuner.isRunning
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val a4Hz: StateFlow<Float> = settings.tunerSettings
        .map { it.a4Hz }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 440f)

    val tunings: StateFlow<List<Tuning>> = a4Hz
        .flatMapLatest { a4 -> tuningRepository.getTunings(a4) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedTuningId: StateFlow<String> = tuningRepository.getSelectedTuningId()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "standard_e")

    val selectedTuning: StateFlow<Tuning?> = combine(tunings, selectedTuningId) { list, id ->
        list.find { it.id == id } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Sticky target note with hysteresis that never resets to null on silence
    private val _targetNote = MutableStateFlow<TuningNote?>(null)
    val targetNote: StateFlow<TuningNote?> = _targetNote.asStateFlow()

    init {
        viewModelScope.launch {
            settings.tunerSettings.collect { tuner.setA4(it.a4Hz) }
        }

        // Reset target note when tuning preset is changed
        viewModelScope.launch {
            selectedTuningId.collect {
                _targetNote.value = null
            }
        }

        // Track target note with hysteresis
        viewModelScope.launch {
            pitch.collect { currentPitch ->
                val tuning = selectedTuning.value
                if (currentPitch == null || tuning == null || tuning.notes.isEmpty()) return@collect

                val f = currentPitch.frequencyHz
                if (f <= 0f) return@collect

                // Find closest candidate by cents distance
                val candidate = tuning.notes.minByOrNull { note ->
                    abs(centsDiff(f, note.targetFrequencyHz))
                } ?: return@collect

                val current = _targetNote.value
                if (current == null) {
                    _targetNote.value = candidate
                } else {
                    val curDiff = abs(centsDiff(f, current.targetFrequencyHz))
                    val candDiff = abs(centsDiff(f, candidate.targetFrequencyHz))
                    // Hysteresis: only switch string if candidate is closer by at least 80 cents,
                    // or if current string is more than 160 cents away
                    if (candDiff < curDiff - 80f || curDiff > 160f) {
                        _targetNote.value = candidate
                    }
                }
            }
        }
    }

    private fun centsDiff(f: Float, targetF: Float): Float {
        if (f <= 0f || targetF <= 0f) return 0f
        return (1200.0 * (ln((f / targetF).toDouble()) / ln(2.0))).toFloat()
    }

    fun start() = tuner.start()
    fun stop() = tuner.stop()

    fun setA4(hz: Float) {
        viewModelScope.launch {
            val value = hz.coerceIn(415f, 466f)
            tuner.setA4(value)
            settings.setA4(value)
        }
    }

    fun selectTuning(tuningId: String) {
        viewModelScope.launch {
            _targetNote.value = null
            tuningRepository.selectTuning(tuningId)
        }
    }

    fun toggleFavorite(tuningId: String) {
        viewModelScope.launch {
            tuningRepository.toggleFavorite(tuningId)
        }
    }

    override fun onCleared() {
        tuner.stop()
        super.onCleared()
    }
}
