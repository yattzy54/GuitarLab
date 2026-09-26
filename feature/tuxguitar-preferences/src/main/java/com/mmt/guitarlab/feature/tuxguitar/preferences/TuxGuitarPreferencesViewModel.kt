package com.mmt.guitarlab.feature.tuxguitar.preferences

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TuxGuitarPreferencesUiState(
    val soundEnabled: Boolean = true,
    val metronomeVolume: Int = 80,
    val showScore: Boolean = true,
    val showTablature: Boolean = true,
    val highlightPlayedBeat: Boolean = true
)

@HiltViewModel
class TuxGuitarPreferencesViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TuxGuitarPreferencesUiState())
    val uiState: StateFlow<TuxGuitarPreferencesUiState> = _uiState.asStateFlow()

    fun updateSoundEnabled(enabled: Boolean) {
        _uiState.update { it.copy(soundEnabled = enabled) }
    }

    fun updateMetronomeVolume(volume: Int) {
        _uiState.update { it.copy(metronomeVolume = volume) }
    }

    fun updateShowScore(show: Boolean) {
        _uiState.update { it.copy(showScore = show) }
    }

    fun updateShowTablature(show: Boolean) {
        _uiState.update { it.copy(showTablature = show) }
    }

    fun updateHighlightPlayedBeat(highlight: Boolean) {
        _uiState.update { it.copy(highlightPlayedBeat = highlight) }
    }
}
