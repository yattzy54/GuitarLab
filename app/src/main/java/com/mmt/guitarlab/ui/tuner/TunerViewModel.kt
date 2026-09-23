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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

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

    // Match detected pitch with closest tuning note in active preset
    val targetNote: StateFlow<TuningNote?> = combine(pitch, selectedTuning) { currentPitch, tuning ->
        if (currentPitch == null || tuning == null) return@combine null
        tuning.notes.minByOrNull { note ->
            abs(currentPitch.midiNote - note.midiNote)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        viewModelScope.launch {
            settings.tunerSettings.collect { tuner.setA4(it.a4Hz) }
        }
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
