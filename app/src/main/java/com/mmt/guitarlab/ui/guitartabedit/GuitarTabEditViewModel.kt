package com.mmt.guitarlab.ui.guitartabedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmt.guitarlab.audio.tab.TabPlaybackEngine
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.NoteDuration
import com.mmt.guitarlab.domain.model.NoteEffect
import com.mmt.guitarlab.domain.model.TabBeat
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.domain.model.TabNote
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import com.mmt.guitarlab.domain.model.TuxGuitarSoundBank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * TuxGuitar-powered ViewModel for the GuitarTabEdit section.
 * Implements TuxGuitar's action processing, multi-track document editing,
 * caret manipulation, tuning presets, and Gervill sound engine playback.
 */
@HiltViewModel
class GuitarTabEditViewModel @Inject constructor(
    private val playbackEngine: TabPlaybackEngine,
) : ViewModel() {

    private val _score = MutableStateFlow<TabScore>(TGDemoSongs.createTuxGuitarTheme())
    val score: StateFlow<TabScore> = _score.asStateFlow()

    private val _caret = MutableStateFlow(TGCaretState())
    val caret: StateFlow<TGCaretState> = _caret.asStateFlow()

    private val _selectedDuration = MutableStateFlow(NoteDuration.EIGHTH)
    val selectedDuration: StateFlow<NoteDuration> = _selectedDuration.asStateFlow()

    private val _viewMode = MutableStateFlow(TGViewMode.DUAL)
    val viewMode: StateFlow<TGViewMode> = _viewMode.asStateFlow()

    private val _soundBank = MutableStateFlow(TuxGuitarSoundBank.GERVILL_CLASSIC)
    val soundBank: StateFlow<TuxGuitarSoundBank> = _soundBank.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isLooping = MutableStateFlow(false)
    val isLooping: StateFlow<Boolean> = _isLooping.asStateFlow()

    private val _isMetronomeEnabled = MutableStateFlow(false)
    val isMetronomeEnabled: StateFlow<Boolean> = _isMetronomeEnabled.asStateFlow()

    private val _zoomLevel = MutableStateFlow(1.0f)
    val zoomLevel: StateFlow<Float> = _zoomLevel.asStateFlow()

    // Undo / Redo history
    private val undoStack = mutableListOf<TabScore>()
    private val redoStack = mutableListOf<TabScore>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    init {
        viewModelScope.launch {
            playbackEngine.isPlaying.collect { playing ->
                _isPlaying.value = playing
            }
        }
        playbackEngine.onPlaybackPositionChanged = { measureIdx: Int, beatIdx: Int ->
            _caret.value = _caret.value.copy(
                measureIndex = measureIdx,
                beatIndex = beatIdx
            )
        }
        playbackEngine.onPlaybackFinished = {
            _isPlaying.value = false
        }
    }

    private fun pushHistory() {
        undoStack.add(_score.value)
        if (undoStack.size > 30) undoStack.removeAt(0)
        redoStack.clear()
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = false
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.add(_score.value)
            _score.value = undoStack.removeAt(undoStack.lastIndex)
            _canUndo.value = undoStack.isNotEmpty()
            _canRedo.value = true
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(_score.value)
            _score.value = redoStack.removeAt(redoStack.lastIndex)
            _canUndo.value = true
            _canRedo.value = redoStack.isNotEmpty()
        }
    }

    // Caret navigation actions
    fun moveLeft() {
        val c = _caret.value
        if (c.beatIndex > 0) {
            _caret.value = c.copy(beatIndex = c.beatIndex - 1)
        } else if (c.measureIndex > 0) {
            val prevMeasure = currentTrack()?.measures?.getOrNull(c.measureIndex - 1)
            val lastBeat = (prevMeasure?.beats?.size?.minus(1))?.coerceAtLeast(0) ?: 0
            _caret.value = c.copy(measureIndex = c.measureIndex - 1, beatIndex = lastBeat)
        }
    }

    fun moveRight() {
        val c = _caret.value
        val track = currentTrack() ?: return
        val currentMeasure = track.measures.getOrNull(c.measureIndex) ?: return
        if (c.beatIndex < currentMeasure.beats.size - 1) {
            _caret.value = c.copy(beatIndex = c.beatIndex + 1)
        } else if (c.measureIndex < track.measures.size - 1) {
            _caret.value = c.copy(measureIndex = c.measureIndex + 1, beatIndex = 0)
        }
    }

    fun moveUp() {
        val c = _caret.value
        if (c.stringIndex > 0) {
            _caret.value = c.copy(stringIndex = c.stringIndex - 1)
            playPreviewForCaret()
        }
    }

    fun moveDown() {
        val c = _caret.value
        val track = currentTrack() ?: return
        if (c.stringIndex < track.stringCount - 1) {
            _caret.value = c.copy(stringIndex = c.stringIndex + 1)
            playPreviewForCaret()
        }
    }

    fun selectCell(measureIdx: Int, beatIdx: Int, stringIdx: Int) {
        val track = currentTrack() ?: return
        val m = measureIdx.coerceIn(0, (track.measures.size - 1).coerceAtLeast(0))
        val maxBeats = track.measures.getOrNull(m)?.beats?.size ?: 1
        val b = beatIdx.coerceIn(0, (maxBeats - 1).coerceAtLeast(0))
        val s = stringIdx.coerceIn(0, track.stringCount - 1)
        _caret.value = _caret.value.copy(measureIndex = m, beatIndex = b, stringIndex = s)
        playPreviewForCaret()
    }

    fun selectString(stringIdx: Int) {
        val track = currentTrack() ?: return
        val s = stringIdx.coerceIn(0, track.stringCount - 1)
        _caret.value = _caret.value.copy(stringIndex = s)
        playPreviewForCaret()
    }

    fun setFret(fret: Int) {
        pushHistory()
        val c = _caret.value
        val currentScore = _score.value
        val track = currentScore.tracks.getOrNull(c.trackIndex) ?: return
        val measure = track.measures.getOrNull(c.measureIndex) ?: return
        val beat = measure.beats.getOrNull(c.beatIndex) ?: return

        // Update note list on current beat
        val remainingNotes = beat.notes.filter { it.stringIndex != c.stringIndex }
        val newNotes = remainingNotes + TabNote(
            stringIndex = c.stringIndex,
            fret = fret,
            effect = NoteEffect.NONE,
            durationBeats = _selectedDuration.value.durationBeats
        )
        val updatedBeat = beat.copy(notes = newNotes, durationType = _selectedDuration.value)
        val updatedBeats = measure.beats.toMutableList()
        updatedBeats[c.beatIndex] = updatedBeat

        val updatedMeasures = track.measures.toMutableList()
        updatedMeasures[c.measureIndex] = measure.copy(beats = updatedBeats)

        val updatedTracks = currentScore.tracks.toMutableList()
        updatedTracks[c.trackIndex] = track.copy(measures = updatedMeasures)

        _score.value = currentScore.copy(tracks = updatedTracks)

        // Play real-time audio feedback with Gervill soundbank
        playFretPreview(c.stringIndex, fret)

        // Auto advance caret
        moveRight()
    }

    fun deleteNoteOrRest() {
        pushHistory()
        val c = _caret.value
        val currentScore = _score.value
        val track = currentScore.tracks.getOrNull(c.trackIndex) ?: return
        val measure = track.measures.getOrNull(c.measureIndex) ?: return
        val beat = measure.beats.getOrNull(c.beatIndex) ?: return

        val noteOnString = beat.notes.find { it.stringIndex == c.stringIndex }
        if (noteOnString != null) {
            val updatedBeat = beat.copy(notes = beat.notes.filter { it.stringIndex != c.stringIndex })
            val updatedBeats = measure.beats.toMutableList()
            updatedBeats[c.beatIndex] = updatedBeat

            val updatedMeasures = track.measures.toMutableList()
            updatedMeasures[c.measureIndex] = measure.copy(beats = updatedBeats)

            val updatedTracks = currentScore.tracks.toMutableList()
            updatedTracks[c.trackIndex] = track.copy(measures = updatedMeasures)
            _score.value = currentScore.copy(tracks = updatedTracks)
        } else if (measure.beats.size > 1) {
            val updatedBeats = measure.beats.toMutableList()
            updatedBeats.removeAt(c.beatIndex)
            val updatedMeasures = track.measures.toMutableList()
            updatedMeasures[c.measureIndex] = measure.copy(beats = updatedBeats)

            val updatedTracks = currentScore.tracks.toMutableList()
            updatedTracks[c.trackIndex] = track.copy(measures = updatedMeasures)
            _score.value = currentScore.copy(tracks = updatedTracks)

            _caret.value = c.copy(beatIndex = (c.beatIndex - 1).coerceAtLeast(0))
        }
    }

    fun insertRest() {
        pushHistory()
        val c = _caret.value
        val currentScore = _score.value
        val track = currentScore.tracks.getOrNull(c.trackIndex) ?: return
        val measure = track.measures.getOrNull(c.measureIndex) ?: return

        val newBeat = TabBeat(
            notes = emptyList(),
            durationBeats = _selectedDuration.value.durationBeats,
            durationType = _selectedDuration.value
        )
        val updatedBeats = measure.beats.toMutableList()
        val insertPos = (c.beatIndex + 1).coerceAtMost(updatedBeats.size)
        updatedBeats.add(insertPos, newBeat)

        val updatedMeasures = track.measures.toMutableList()
        updatedMeasures[c.measureIndex] = measure.copy(beats = updatedBeats)

        val updatedTracks = currentScore.tracks.toMutableList()
        updatedTracks[c.trackIndex] = track.copy(measures = updatedMeasures)
        _score.value = currentScore.copy(tracks = updatedTracks)

        _caret.value = c.copy(beatIndex = insertPos)
    }

    fun toggleEffect(effect: NoteEffect) {
        pushHistory()
        val c = _caret.value
        val currentScore = _score.value
        val track = currentScore.tracks.getOrNull(c.trackIndex) ?: return
        val measure = track.measures.getOrNull(c.measureIndex) ?: return
        val beat = measure.beats.getOrNull(c.beatIndex) ?: return
        val note = beat.notes.find { it.stringIndex == c.stringIndex } ?: return

        val newEffect = if (note.effect == effect) NoteEffect.NONE else effect
        val updatedNotes = beat.notes.map {
            if (it.stringIndex == c.stringIndex) it.copy(effect = newEffect) else it
        }
        val updatedBeat = beat.copy(notes = updatedNotes)
        val updatedBeats = measure.beats.toMutableList()
        updatedBeats[c.beatIndex] = updatedBeat

        val updatedMeasures = track.measures.toMutableList()
        updatedMeasures[c.measureIndex] = measure.copy(beats = updatedBeats)

        val updatedTracks = currentScore.tracks.toMutableList()
        updatedTracks[c.trackIndex] = track.copy(measures = updatedMeasures)
        _score.value = currentScore.copy(tracks = updatedTracks)
    }

    fun incrementSemitone() {
        pushHistory()
        val c = _caret.value
        val currentScore = _score.value
        val track = currentScore.tracks.getOrNull(c.trackIndex) ?: return
        val measure = track.measures.getOrNull(c.measureIndex) ?: return
        val beat = measure.beats.getOrNull(c.beatIndex) ?: return
        val note = beat.notes.find { it.stringIndex == c.stringIndex } ?: return

        val newFret = (note.fret + 1).coerceAtMost(24)
        val updatedNotes = beat.notes.map {
            if (it.stringIndex == c.stringIndex) it.copy(fret = newFret) else it
        }
        val updatedBeat = beat.copy(notes = updatedNotes)
        val updatedBeats = measure.beats.toMutableList()
        updatedBeats[c.beatIndex] = updatedBeat

        val updatedMeasures = track.measures.toMutableList()
        updatedMeasures[c.measureIndex] = measure.copy(beats = updatedBeats)

        val updatedTracks = currentScore.tracks.toMutableList()
        updatedTracks[c.trackIndex] = track.copy(measures = updatedMeasures)
        _score.value = currentScore.copy(tracks = updatedTracks)

        playFretPreview(c.stringIndex, newFret)
    }

    fun decrementSemitone() {
        pushHistory()
        val c = _caret.value
        val currentScore = _score.value
        val track = currentScore.tracks.getOrNull(c.trackIndex) ?: return
        val measure = track.measures.getOrNull(c.measureIndex) ?: return
        val beat = measure.beats.getOrNull(c.beatIndex) ?: return
        val note = beat.notes.find { it.stringIndex == c.stringIndex } ?: return

        val newFret = (note.fret - 1).coerceAtLeast(0)
        val updatedNotes = beat.notes.map {
            if (it.stringIndex == c.stringIndex) it.copy(fret = newFret) else it
        }
        val updatedBeat = beat.copy(notes = updatedNotes)
        val updatedBeats = measure.beats.toMutableList()
        updatedBeats[c.beatIndex] = updatedBeat

        val updatedMeasures = track.measures.toMutableList()
        updatedMeasures[c.measureIndex] = measure.copy(beats = updatedBeats)

        val updatedTracks = currentScore.tracks.toMutableList()
        updatedTracks[c.trackIndex] = track.copy(measures = updatedMeasures)
        _score.value = currentScore.copy(tracks = updatedTracks)

        playFretPreview(c.stringIndex, newFret)
    }

    fun setDuration(duration: NoteDuration) {
        _selectedDuration.value = duration
        val c = _caret.value
        val currentScore = _score.value
        val track = currentScore.tracks.getOrNull(c.trackIndex) ?: return
        val measure = track.measures.getOrNull(c.measureIndex) ?: return
        val beat = measure.beats.getOrNull(c.beatIndex) ?: return

        pushHistory()
        val updatedBeat = beat.copy(durationBeats = duration.durationBeats, durationType = duration)
        val updatedBeats = measure.beats.toMutableList()
        updatedBeats[c.beatIndex] = updatedBeat

        val updatedMeasures = track.measures.toMutableList()
        updatedMeasures[c.measureIndex] = measure.copy(beats = updatedBeats)

        val updatedTracks = currentScore.tracks.toMutableList()
        updatedTracks[c.trackIndex] = track.copy(measures = updatedMeasures)
        _score.value = currentScore.copy(tracks = updatedTracks)
    }

    fun setViewMode(mode: TGViewMode) {
        _viewMode.value = mode
    }

    fun setSoundBank(bank: TuxGuitarSoundBank) {
        _soundBank.value = bank
        playbackEngine.setSoundBank(bank)
    }

    fun setZoom(delta: Float) {
        _zoomLevel.value = (_zoomLevel.value + delta).coerceIn(0.6f, 2.5f)
    }

    fun resetZoom() {
        _zoomLevel.value = 1.0f
    }

    // Transport playback
    fun togglePlay() {
        if (_isPlaying.value) {
            playbackEngine.pause()
            _isPlaying.value = false
        } else {
            val sc = _score.value
            val m = _caret.value.measureIndex
            if (_isLooping.value) {
                playbackEngine.setLoop(m, m)
            } else {
                playbackEngine.clearLoop()
            }
            playbackEngine.setMetronomeEnabled(_isMetronomeEnabled.value)
            playbackEngine.setSoundBank(_soundBank.value)
            playbackEngine.play(
                score = sc,
                activeTrackIndex = _caret.value.trackIndex,
                startMeasureIndex = _caret.value.measureIndex,
                startBeatIndex = _caret.value.beatIndex
            )
            _isPlaying.value = true
        }
    }

    fun stop() {
        playbackEngine.stop()
        _isPlaying.value = false
        _caret.value = _caret.value.copy(measureIndex = 0, beatIndex = 0)
    }

    fun toggleLoop() {
        _isLooping.value = !_isLooping.value
        val m = _caret.value.measureIndex
        if (_isLooping.value) {
            playbackEngine.setLoop(m, m)
        } else {
            playbackEngine.clearLoop()
        }
    }

    fun toggleMetronome() {
        _isMetronomeEnabled.value = !_isMetronomeEnabled.value
        playbackEngine.setMetronomeEnabled(_isMetronomeEnabled.value)
    }

    // Measure manipulations
    fun addMeasure() {
        pushHistory()
        val currentScore = _score.value
        val numMeasures = currentScore.tracks.firstOrNull()?.measures?.size ?: 0
        val newMeasureNumber = numMeasures + 1

        val updatedTracks = currentScore.tracks.map { track ->
            val emptyBeats = listOf(
                TabBeat(durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
                TabBeat(durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
                TabBeat(durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
                TabBeat(durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            )
            val newMeasure = TabMeasure(number = newMeasureNumber, beats = emptyBeats)
            track.copy(measures = track.measures + newMeasure)
        }
        _score.value = currentScore.copy(tracks = updatedTracks)
        _caret.value = _caret.value.copy(measureIndex = numMeasures, beatIndex = 0)
    }

    fun removeMeasure() {
        val currentScore = _score.value
        val track = currentScore.tracks.firstOrNull() ?: return
        if (track.measures.size <= 1) return

        pushHistory()
        val c = _caret.value
        val updatedTracks = currentScore.tracks.map { tr ->
            val updatedMeasures = tr.measures.toMutableList()
            if (c.measureIndex in updatedMeasures.indices) {
                updatedMeasures.removeAt(c.measureIndex)
            }
            // Renumber
            updatedMeasures.mapIndexed { i, m -> m.copy(number = i + 1) }
            tr.copy(measures = updatedMeasures)
        }
        _score.value = currentScore.copy(tracks = updatedTracks)
        _caret.value = c.copy(measureIndex = (c.measureIndex - 1).coerceAtLeast(0), beatIndex = 0)
    }

    fun setTimeSignature(num: Int, den: Int) {
        pushHistory()
        val currentScore = _score.value
        val c = _caret.value
        val updatedTracks = currentScore.tracks.map { track ->
            val updatedMeasures = track.measures.mapIndexed { idx, m ->
                if (idx >= c.measureIndex) m.copy(timeSignatureNumerator = num, timeSignatureDenominator = den) else m
            }
            track.copy(measures = updatedMeasures)
        }
        _score.value = currentScore.copy(
            timeSignatureNumerator = num,
            timeSignatureDenominator = den,
            tracks = updatedTracks
        )
    }

    fun setTempo(bpm: Int) {
        pushHistory()
        val currentScore = _score.value
        _score.value = currentScore.copy(tempo = bpm)
        playbackEngine.setTempo(bpm)
    }

    // Track manipulations
    fun selectTrack(index: Int) {
        val currentScore = _score.value
        if (index in currentScore.tracks.indices) {
            val track = currentScore.tracks[index]
            _caret.value = _caret.value.copy(
                trackIndex = index,
                stringIndex = _caret.value.stringIndex.coerceIn(0, track.stringCount - 1)
            )
        }
    }

    fun addTrack(name: String, instrument: InstrumentType) {
        pushHistory()
        val currentScore = _score.value
        val measureCount = currentScore.tracks.firstOrNull()?.measures?.size ?: 4
        val emptyMeasures = (1..measureCount).map { num ->
            val emptyBeats = listOf(
                TabBeat(durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
                TabBeat(durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
                TabBeat(durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
                TabBeat(durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            )
            TabMeasure(number = num, beats = emptyBeats)
        }
        val newTrack = TabTrack(
            name = name,
            instrumentType = instrument,
            stringCount = instrument.defaultStringCount,
            stringLabels = instrument.defaultStringLabels,
            volume = 0.9f,
            measures = emptyMeasures
        )
        _score.value = currentScore.copy(tracks = currentScore.tracks + newTrack)
        selectTrack(currentScore.tracks.size)
    }

    fun removeTrack(index: Int) {
        val currentScore = _score.value
        if (currentScore.tracks.size <= 1) return
        pushHistory()
        val updated = currentScore.tracks.toMutableList()
        updated.removeAt(index)
        _score.value = currentScore.copy(tracks = updated)
        selectTrack((index - 1).coerceAtLeast(0))
    }

    fun setTrackTuning(preset: TGTuningPreset) {
        pushHistory()
        val currentScore = _score.value
        val c = _caret.value
        val track = currentScore.tracks.getOrNull(c.trackIndex) ?: return

        val updatedTrack = track.copy(
            tuningName = preset.name,
            stringCount = preset.stringCount,
            stringLabels = preset.labels
        )
        val updatedTracks = currentScore.tracks.toMutableList()
        updatedTracks[c.trackIndex] = updatedTrack
        _score.value = currentScore.copy(tracks = updatedTracks)
        _caret.value = c.copy(stringIndex = c.stringIndex.coerceIn(0, preset.stringCount - 1))
    }

    fun setTrackVolume(index: Int, vol: Float) {
        val currentScore = _score.value
        val updatedTracks = currentScore.tracks.mapIndexed { idx, tr ->
            if (idx == index) tr.copy(volume = vol.coerceIn(0f, 1f)) else tr
        }
        _score.value = currentScore.copy(tracks = updatedTracks)
    }

    fun setTrackPan(index: Int, pan: Float) {
        val currentScore = _score.value
        val updatedTracks = currentScore.tracks.mapIndexed { idx, tr ->
            if (idx == index) tr.copy(pan = pan.coerceIn(-1f, 1f)) else tr
        }
        _score.value = currentScore.copy(tracks = updatedTracks)
    }

    fun toggleMuteTrack(index: Int) {
        val currentScore = _score.value
        val updatedTracks = currentScore.tracks.mapIndexed { idx, tr ->
            if (idx == index) tr.copy(isMuted = !tr.isMuted) else tr
        }
        _score.value = currentScore.copy(tracks = updatedTracks)
    }

    fun toggleSoloTrack(index: Int) {
        val currentScore = _score.value
        val updatedTracks = currentScore.tracks.mapIndexed { idx, tr ->
            if (idx == index) tr.copy(isSolo = !tr.isSolo) else tr
        }
        _score.value = currentScore.copy(tracks = updatedTracks)
    }

    fun updateScoreInfo(title: String, artist: String) {
        pushHistory()
        _score.value = _score.value.copy(title = title, artist = artist)
    }

    fun loadScore(newScore: TabScore) {
        pushHistory()
        _score.value = newScore
        _caret.value = TGCaretState(trackIndex = 0, measureIndex = 0, beatIndex = 0, stringIndex = 0)
    }

    fun loadDemoTuxGuitar() {
        loadScore(TGDemoSongs.createTuxGuitarTheme())
    }

    fun loadDemoRock() {
        loadScore(TGDemoSongs.createRockRiff())
    }

    fun newBlankScore() {
        loadScore(TGDemoSongs.createBlankScore())
    }

    fun playFretPreview(stringIndex: Int, fret: Int) {
        val track = currentTrack() ?: return
        playbackEngine.playPreviewFret(track, stringIndex, fret)
    }

    private fun playPreviewForCaret() {
        val c = _caret.value
        val track = currentTrack() ?: return
        val beat = track.measures.getOrNull(c.measureIndex)?.beats?.getOrNull(c.beatIndex)
        val note = beat?.notes?.find { it.stringIndex == c.stringIndex }
        val fret = note?.fret ?: 0
        val effect = note?.effect ?: NoteEffect.NONE
        playbackEngine.playPreviewFret(track, c.stringIndex, fret, effect)
    }

    private fun currentTrack(): TabTrack? {
        val currentScore = _score.value
        return currentScore.tracks.getOrNull(_caret.value.trackIndex) ?: currentScore.tracks.firstOrNull()
    }

    override fun onCleared() {
        super.onCleared()
        playbackEngine.stop()
    }
}
