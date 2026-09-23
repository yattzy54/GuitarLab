package com.mmt.guitarlab.ui.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmt.guitarlab.domain.model.ChordFormula
import com.mmt.guitarlab.domain.model.FretboardMode
import com.mmt.guitarlab.domain.model.MusicTheory
import com.mmt.guitarlab.domain.model.ScaleFormula
import com.mmt.guitarlab.domain.model.Tuning
import com.mmt.guitarlab.domain.repository.TuningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FretPosition(val stringIndex: Int, val fret: Int, val midiNote: Int)

@HiltViewModel
class FretboardViewModel @Inject constructor(
    private val tuningRepository: TuningRepository,
) : ViewModel() {

    val availableTunings: StateFlow<List<Tuning>> = tuningRepository.getTunings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedTuning: StateFlow<Tuning?> = combine(
        tuningRepository.getTunings(),
        tuningRepository.getSelectedTuningId(),
    ) { list, id ->
        list.find { it.id == id } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _mode = MutableStateFlow(FretboardMode.CHORD_SCALE_FINDER)
    val mode: StateFlow<FretboardMode> = _mode.asStateFlow()

    private val _rootNote = MutableStateFlow("C")
    val rootNote: StateFlow<String> = _rootNote.asStateFlow()

    private val _selectedChord = MutableStateFlow<ChordFormula?>(MusicTheory.chordFormulas.first())
    val selectedChord: StateFlow<ChordFormula?> = _selectedChord.asStateFlow()

    private val _selectedScale = MutableStateFlow<ScaleFormula?>(null)
    val selectedScale: StateFlow<ScaleFormula?> = _selectedScale.asStateFlow()

    // Interactive Pressed Frets for Reverse Lookup
    private val _pressedFrets = MutableStateFlow<Set<FretPosition>>(emptySet())
    val pressedFrets: StateFlow<Set<FretPosition>> = _pressedFrets.asStateFlow()

    val detectedChords: StateFlow<List<String>> = _pressedFrets.combine(_mode) { frets, currentMode ->
        if (currentMode == FretboardMode.REVERSE_LOOKUP && frets.isNotEmpty()) {
            MusicTheory.reverseLookupChord(frets.map { it.midiNote })
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectTuning(id: String) {
        viewModelScope.launch {
            tuningRepository.setSelectedTuningId(id)
        }
    }

    fun setMode(newMode: FretboardMode) {
        _mode.value = newMode
    }

    fun setRootNote(note: String) {
        _rootNote.value = note
    }

    fun selectChord(chord: ChordFormula?) {
        _selectedChord.value = chord
        if (chord != null) _selectedScale.value = null
    }

    fun selectScale(scale: ScaleFormula?) {
        _selectedScale.value = scale
        if (scale != null) _selectedChord.value = null
    }

    fun toggleFret(stringIndex: Int, fret: Int, midiNote: Int) {
        val current = _pressedFrets.value.toMutableSet()
        val pos = FretPosition(stringIndex, fret, midiNote)
        if (current.contains(pos)) {
            current.remove(pos)
        } else {
            // allow max 1 note per string in reverse lookup
            current.removeAll { it.stringIndex == stringIndex }
            current.add(pos)
        }
        _pressedFrets.value = current
    }

    fun clearPressedFrets() {
        _pressedFrets.value = emptySet()
    }
}
