package com.mmt.guitarlab.ui.tab

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmt.guitarlab.audio.tab.TabPlaybackEngine
import com.mmt.guitarlab.data.db.TabProjectEntity
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.NoteDuration
import com.mmt.guitarlab.domain.model.NoteEffect
import com.mmt.guitarlab.domain.model.TabBeat
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.domain.model.TabNote
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import com.mmt.guitarlab.domain.repository.TabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class TabViewModel @Inject constructor(
    private val tabRepository: TabRepository,
    private val playbackEngine: TabPlaybackEngine,
    private val songsterrApi: com.mmt.guitarlab.data.remote.SongsterrApiService,
) : ViewModel() {

    // Online Songsterr Catalog state
    private val _searchResults = MutableStateFlow<List<com.mmt.guitarlab.data.remote.SongsterrSearchResult>>(emptyList())
    val searchResults: StateFlow<List<com.mmt.guitarlab.data.remote.SongsterrSearchResult>> = _searchResults.asStateFlow()

    private val _isSearchingOnline = MutableStateFlow(false)
    val isSearchingOnline: StateFlow<Boolean> = _isSearchingOnline.asStateFlow()

    private val _isLoadingOnlineSong = MutableStateFlow(false)
    val isLoadingOnlineSong: StateFlow<Boolean> = _isLoadingOnlineSong.asStateFlow()

    fun searchOnlineSongs(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isSearchingOnline.value = true
            songsterrApi.searchSongs(query)
                .onSuccess {
                    _searchResults.value = it
                    _isSearchingOnline.value = false
                }
                .onFailure {
                    _isSearchingOnline.value = false
                    _statusMessage.value = "Ошибка поиска: ${it.message}"
                }
        }
    }

    fun loadOnlineSong(songId: Int, specificTrackIdx: Int? = null, onLoaded: (() -> Unit)? = null) {
        viewModelScope.launch {
            _isLoadingOnlineSong.value = true
            _statusMessage.value = "Загрузка из каталога Songsterr..."
            playbackEngine.stop()
            songsterrApi.fetchSongScore(songId, specificTrackIdx)
                .onSuccess { newScore ->
                    _score.value = newScore
                    _selectedTrackIndex.value = 0
                    _selectedMeasureIndex.value = 0
                    _selectedBeatIndex.value = 0
                    _isLoadingOnlineSong.value = false
                    _statusMessage.value = "Загружено: ${newScore.title} — ${newScore.artist}"
                    onLoaded?.invoke()
                }
                .onFailure {
                    _isLoadingOnlineSong.value = false
                    _statusMessage.value = "Ошибка загрузки: ${it.message}"
                }
        }
    }

    private val _score = MutableStateFlow<TabScore?>(createSampleScore())
    val score: StateFlow<TabScore?> = _score.asStateFlow()

    private val _zoomScale = MutableStateFlow(1.0f)
    val zoomScale: StateFlow<Float> = _zoomScale.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _exportedMidiFile = MutableStateFlow<File?>(null)
    val exportedMidiFile: StateFlow<File?> = _exportedMidiFile.asStateFlow()

    val savedProjects: StateFlow<List<TabProjectEntity>> = tabRepository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Playback state
    val isPlaying: StateFlow<Boolean> = playbackEngine.isPlaying
    val currentMeasureIndex: StateFlow<Int> = playbackEngine.currentMeasureIndex
    val currentBeatIndex: StateFlow<Int> = playbackEngine.currentBeatIndex
    val speedMultiplier: StateFlow<Float> = playbackEngine.speedMultiplier

    // Selection & Editing state
    private val _selectedTrackIndex = MutableStateFlow(0)
    val selectedTrackIndex: StateFlow<Int> = _selectedTrackIndex.asStateFlow()

    private val _selectedMeasureIndex = MutableStateFlow(0)
    val selectedMeasureIndex: StateFlow<Int> = _selectedMeasureIndex.asStateFlow()

    private val _selectedBeatIndex = MutableStateFlow(0)
    val selectedBeatIndex: StateFlow<Int> = _selectedBeatIndex.asStateFlow()

    private val _selectedStringIndex = MutableStateFlow(0)
    val selectedStringIndex: StateFlow<Int> = _selectedStringIndex.asStateFlow()

    private val _activeEffect = MutableStateFlow(NoteEffect.NONE)
    val activeEffect: StateFlow<NoteEffect> = _activeEffect.asStateFlow()

    private val _activeDuration = MutableStateFlow(NoteDuration.EIGHTH)
    val activeDuration: StateFlow<NoteDuration> = _activeDuration.asStateFlow()

    fun setZoom(scale: Float) {
        _zoomScale.value = scale.coerceIn(0.5f, 2.5f)
    }

    fun selectTrack(index: Int) {
        val currentScore = _score.value ?: return
        val clampedIdx = index.coerceIn(0, currentScore.tracks.lastIndex.coerceAtLeast(0))
        _selectedTrackIndex.value = clampedIdx

        // Unmute target track so it is always audible
        val tracks = currentScore.tracks.toMutableList()
        val targetTrack = tracks[clampedIdx]
        var scoreUpdated = false
        if (targetTrack.isMuted) {
            tracks[clampedIdx] = targetTrack.copy(isMuted = false)
            scoreUpdated = true
        }
        val hasSoloOther = tracks.any { it.id != targetTrack.id && it.isSolo }
        if (hasSoloOther && !targetTrack.isSolo) {
            tracks[clampedIdx] = tracks[clampedIdx].copy(isSolo = true)
            scoreUpdated = true
        }
        if (scoreUpdated) {
            _score.value = currentScore.copy(tracks = tracks)
        }

        // Get current playback position to keep track synchronized
        val curM = playbackEngine.currentMeasureIndex.value
        val curB = playbackEngine.currentBeatIndex.value

        // Switch playback engine to the new active track and immediately start/continue playback
        val activeScore = _score.value ?: currentScore
        val activeTrk = activeScore.tracks.getOrNull(clampedIdx)
        val validM = curM.coerceIn(0, (activeTrk?.measures?.lastIndex ?: 0).coerceAtLeast(0))
        val measureBeats = activeTrk?.measures?.getOrNull(validM)?.beats?.size ?: 1
        val validB = curB.coerceIn(0, (measureBeats - 1).coerceAtLeast(0))

        playbackEngine.seekTo(validM, validB)
        playbackEngine.play(
            score = activeScore,
            activeTrackIndex = clampedIdx,
            startMeasureIndex = validM,
            startBeatIndex = validB,
        )
    }

    fun toggleMuteTrack(index: Int) {
        val currentScore = _score.value ?: return
        val tracks = currentScore.tracks.toMutableList()
        if (index in tracks.indices) {
            val t = tracks[index]
            tracks[index] = t.copy(isMuted = !t.isMuted)
            _score.value = currentScore.copy(tracks = tracks)
        }
    }

    fun toggleSoloTrack(index: Int) {
        val currentScore = _score.value ?: return
        val tracks = currentScore.tracks.toMutableList()
        if (index in tracks.indices) {
            val t = tracks[index]
            tracks[index] = t.copy(isSolo = !t.isSolo)
            _score.value = currentScore.copy(tracks = tracks)
        }
    }

    fun setTrackVolume(index: Int, volume: Float) {
        val currentScore = _score.value ?: return
        val tracks = currentScore.tracks.toMutableList()
        if (index in tracks.indices) {
            tracks[index] = tracks[index].copy(volume = volume.coerceIn(0f, 1f))
            _score.value = currentScore.copy(tracks = tracks)
        }
    }

    fun setTrackPan(index: Int, pan: Float) {
        val currentScore = _score.value ?: return
        val tracks = currentScore.tracks.toMutableList()
        if (index in tracks.indices) {
            tracks[index] = tracks[index].copy(pan = pan.coerceIn(-1f, 1f))
            _score.value = currentScore.copy(tracks = tracks)
        }
    }

    fun addTrack(name: String, instrumentType: InstrumentType) {
        val currentScore = _score.value ?: return
        val tracks = currentScore.tracks.toMutableList()
        val templateMeasures = currentScore.tracks.firstOrNull()?.measures?.mapIndexed { idx, _ ->
            TabMeasure(
                number = idx + 1,
                beats = listOf(
                    TabBeat(notes = listOf(TabNote(0, 0)), startBeat = 0f),
                    TabBeat(startBeat = 0.5f),
                    TabBeat(startBeat = 1.0f),
                    TabBeat(startBeat = 1.5f),
                ),
            )
        } ?: createSampleMeasures()

        tracks.add(
            TabTrack(
                name = name.ifBlank { instrumentType.displayName },
                instrumentType = instrumentType,
                measures = templateMeasures,
            ),
        )
        _score.value = currentScore.copy(tracks = tracks)
        _selectedTrackIndex.value = tracks.lastIndex
    }

    fun selectCell(measureIdx: Int, beatIdx: Int, stringIdx: Int) {
        _selectedMeasureIndex.value = measureIdx
        _selectedBeatIndex.value = beatIdx
        _selectedStringIndex.value = stringIdx
    }

    fun togglePlay() {
        val currentScore = _score.value ?: return
        if (isPlaying.value) {
            playbackEngine.pause()
        } else {
            playbackEngine.play(currentScore, _selectedTrackIndex.value, _selectedMeasureIndex.value)
        }
    }

    fun stopPlayback() {
        playbackEngine.stop()
    }

    fun setSpeedMultiplier(speed: Float) {
        playbackEngine.setSpeed(speed)
    }

    fun seekPlayback(measureIdx: Int, beatIdx: Int = 0) {
        val currentScore = _score.value ?: return
        _selectedMeasureIndex.value = measureIdx
        _selectedBeatIndex.value = beatIdx
        if (isPlaying.value) {
            playbackEngine.play(currentScore, _selectedTrackIndex.value, measureIdx, beatIdx)
        } else {
            playbackEngine.seekTo(measureIdx, beatIdx)
        }
    }

    fun setLoop(startMeasure: Int, endMeasure: Int) {
        playbackEngine.setLoop(startMeasure, endMeasure)
    }

    fun clearLoop() {
        playbackEngine.clearLoop()
    }

    fun setActiveEffect(effect: NoteEffect) {
        _activeEffect.value = effect
    }

    fun setActiveDuration(duration: NoteDuration) {
        _activeDuration.value = duration
    }

    fun loadFromFile(context: Context, uri: Uri) {
        val fileName = getFileName(context, uri)
        viewModelScope.launch {
            _statusMessage.value = "Loading $fileName..."
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    tabRepository.parseTab(stream, fileName)
                }
            }.onSuccess { result ->
                if (result != null && result.isSuccess) {
                    _score.value = result.getOrNull()
                    _selectedTrackIndex.value = 0
                    _selectedMeasureIndex.value = 0
                    _selectedBeatIndex.value = 0
                    _statusMessage.value = "Loaded $fileName"
                } else {
                    _statusMessage.value = "Failed to parse $fileName"
                }
            }.onFailure {
                _statusMessage.value = "Error reading file: ${it.message}"
            }
        }
    }

    fun updateFretAtSelectedCell(newFret: Int?) {
        val currentScore = _score.value ?: return
        val mIdx = _selectedMeasureIndex.value
        val bIdx = _selectedBeatIndex.value
        val sIdx = _selectedStringIndex.value

        val tracks = currentScore.tracks.toMutableList()
        val trackIdx = _selectedTrackIndex.value.coerceIn(0, tracks.lastIndex)
        val activeTrack = tracks[trackIdx]

        val measures = activeTrack.measures.toMutableList()
        if (mIdx !in measures.indices) return
        val activeMeasure = measures[mIdx]

        val beats = activeMeasure.beats.toMutableList()
        if (bIdx !in beats.indices) return
        val activeBeat = beats[bIdx]

        val notes = activeBeat.notes.toMutableList()
        notes.removeAll { it.stringIndex == sIdx }

        if (newFret != null) {
            notes.add(
                TabNote(
                    stringIndex = sIdx,
                    fret = newFret.coerceIn(0, 24),
                    effect = _activeEffect.value,
                    durationBeats = _activeDuration.value.durationBeats,
                ),
            )
        }

        beats[bIdx] = activeBeat.copy(notes = notes, durationType = _activeDuration.value)
        measures[mIdx] = activeMeasure.copy(beats = beats)
        tracks[trackIdx] = activeTrack.copy(measures = measures)

        _score.value = currentScore.copy(tracks = tracks)
        autoSaveToDb()
    }

    fun addBeat() {
        val currentScore = _score.value ?: return
        val mIdx = _selectedMeasureIndex.value
        val tIdx = _selectedTrackIndex.value.coerceIn(0, currentScore.tracks.lastIndex)

        val tracks = currentScore.tracks.toMutableList()
        val activeTrack = tracks[tIdx]
        val measures = activeTrack.measures.toMutableList()
        if (mIdx !in measures.indices) return

        val activeMeasure = measures[mIdx]
        val beats = activeMeasure.beats.toMutableList()
        val nextStartBeat = (beats.lastOrNull()?.startBeat ?: 0f) + _activeDuration.value.durationBeats
        beats.add(TabBeat(startBeat = nextStartBeat, durationBeats = _activeDuration.value.durationBeats, durationType = _activeDuration.value))

        measures[mIdx] = activeMeasure.copy(beats = beats)
        tracks[tIdx] = activeTrack.copy(measures = measures)
        _score.value = currentScore.copy(tracks = tracks)
        autoSaveToDb()
    }

    fun addMeasure() {
        val currentScore = _score.value ?: return
        val tIdx = _selectedTrackIndex.value.coerceIn(0, currentScore.tracks.lastIndex)

        val tracks = currentScore.tracks.toMutableList()
        val activeTrack = tracks[tIdx]
        val measures = activeTrack.measures.toMutableList()

        val nextMeasureNum = measures.size + 1
        measures.add(
            TabMeasure(
                number = nextMeasureNum,
                beats = listOf(
                    TabBeat(notes = listOf(TabNote(0, 0)), startBeat = 0f),
                    TabBeat(startBeat = 0.5f),
                    TabBeat(startBeat = 1.0f),
                    TabBeat(startBeat = 1.5f),
                ),
            ),
        )

        tracks[tIdx] = activeTrack.copy(measures = measures)
        _score.value = currentScore.copy(tracks = tracks)
        autoSaveToDb()
    }

    fun moveCaretUp() {
        val s = _score.value ?: return
        val track = s.tracks.getOrNull(_selectedTrackIndex.value) ?: return
        val maxString = (track.stringCount - 1).coerceAtLeast(0)
        _selectedStringIndex.value = (_selectedStringIndex.value - 1).coerceIn(0, maxString)
    }

    fun moveCaretDown() {
        val s = _score.value ?: return
        val track = s.tracks.getOrNull(_selectedTrackIndex.value) ?: return
        val maxString = (track.stringCount - 1).coerceAtLeast(0)
        _selectedStringIndex.value = (_selectedStringIndex.value + 1).coerceIn(0, maxString)
    }

    fun moveCaretLeft() {
        val s = _score.value ?: return
        val track = s.tracks.getOrNull(_selectedTrackIndex.value) ?: return
        val mIdx = _selectedMeasureIndex.value
        val bIdx = _selectedBeatIndex.value
        if (bIdx > 0) {
            _selectedBeatIndex.value = bIdx - 1
        } else if (mIdx > 0) {
            val prevM = track.measures.getOrNull(mIdx - 1)
            _selectedMeasureIndex.value = mIdx - 1
            _selectedBeatIndex.value = (prevM?.beats?.lastIndex ?: 0).coerceAtLeast(0)
        }
    }

    fun moveCaretRight() {
        val s = _score.value ?: return
        val track = s.tracks.getOrNull(_selectedTrackIndex.value) ?: return
        val mIdx = _selectedMeasureIndex.value
        val bIdx = _selectedBeatIndex.value
        val currentMeasure = track.measures.getOrNull(mIdx)
        if (currentMeasure != null && bIdx < currentMeasure.beats.lastIndex) {
            _selectedBeatIndex.value = bIdx + 1
        } else if (mIdx < track.measures.lastIndex) {
            _selectedMeasureIndex.value = mIdx + 1
            _selectedBeatIndex.value = 0
        }
    }

    fun cycleDuration(step: Int) {
        val all = NoteDuration.entries
        val current = _activeDuration.value
        val currentIdx = all.indexOf(current)
        val nextIdx = (currentIdx + step).coerceIn(0, all.lastIndex)
        _activeDuration.value = all[nextIdx]
    }

    fun deleteTrack(index: Int) {
        val s = _score.value ?: return
        if (s.tracks.size <= 1) return
        val tracks = s.tracks.toMutableList()
        tracks.removeAt(index.coerceIn(0, tracks.lastIndex))
        _score.value = s.copy(tracks = tracks)
        _selectedTrackIndex.value = _selectedTrackIndex.value.coerceIn(0, tracks.lastIndex)
    }

    fun updateSongInfo(title: String, artist: String, tempo: Int) {
        val s = _score.value ?: return
        _score.value = s.copy(
            title = title.ifBlank { s.title },
            artist = artist.ifBlank { s.artist },
            tempo = tempo.coerceIn(30, 320)
        )
        autoSaveToDb()
    }

    fun saveProjectToDb() {
        val currentScore = _score.value ?: return
        viewModelScope.launch {
            tabRepository.saveProject(currentScore)
                .onSuccess { _statusMessage.value = "Project auto-saved" }
                .onFailure { _statusMessage.value = "Failed to save project: ${it.message}" }
        }
    }

    fun loadProjectFromDb(id: String) {
        viewModelScope.launch {
            tabRepository.loadProject(id)
                .onSuccess {
                    _score.value = it
                    _selectedTrackIndex.value = 0
                    _selectedMeasureIndex.value = 0
                    _selectedBeatIndex.value = 0
                    _statusMessage.value = "Loaded project: ${it.title}"
                }
                .onFailure { _statusMessage.value = "Failed to load project: ${it.message}" }
        }
    }

    fun deleteProjectFromDb(project: TabProjectEntity) {
        viewModelScope.launch {
            tabRepository.deleteProject(project)
        }
    }

    private fun autoSaveToDb() {
        saveProjectToDb()
    }

    fun saveScore(context: Context, uri: Uri) {
        val currentScore = _score.value ?: return
        viewModelScope.launch {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    val text = buildString {
                        append("Title: ${currentScore.title}\n")
                        append("Artist: ${currentScore.artist}\n")
                        append("Tempo: ${currentScore.tempo}\n\n")
                        currentScore.tracks.forEach { track ->
                            append("[Track: ${track.name} (${track.instrumentType.name})]\n")
                            track.measures.forEach { m ->
                                append("M${m.number}: ")
                                m.beats.forEach { b ->
                                    val notesStr = b.notes.joinToString(",") { "S${it.stringIndex}:${it.fret}${it.effect.symbol}" }
                                    append("[$notesStr] ")
                                }
                                append("\n")
                            }
                        }
                    }
                    stream.write(text.toByteArray(Charsets.UTF_8))
                }
            }.onSuccess {
                _statusMessage.value = "Tab saved successfully"
            }.onFailure {
                _statusMessage.value = "Failed to save: ${it.message}"
            }
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var name: String? = null
        if (uri.scheme == "content") {
            runCatching {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (index != -1) {
                            name = cursor.getString(index)
                        }
                    }
                }
            }
        }
        if (name == null) {
            name = uri.path
            val cut = name?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                name = name?.substring(cut + 1)
            }
        }
        return name ?: "Guitar Pro Tab"
    }

    fun loadAsciiText(text: String, title: String) {
        viewModelScope.launch {
            tabRepository.parseAsciiText(text, title)
                .onSuccess {
                    _score.value = it
                    _statusMessage.value = "Text tab parsed"
                }
                .onFailure {
                    _statusMessage.value = "Failed to parse text tab"
                }
        }
    }

    fun exportToMidi(context: Context) {
        val currentScore = _score.value ?: return
        viewModelScope.launch {
            _statusMessage.value = "Exporting MIDI..."
            val dir = File(context.getExternalFilesDir(null), "exported_midi")
            if (!dir.exists()) dir.mkdirs()
            val fileName = "${currentScore.title.replace(" ", "_")}.mid"
            val targetFile = File(dir, fileName)

            tabRepository.exportMidi(currentScore, targetFile)
                .onSuccess { file ->
                    _exportedMidiFile.value = file
                    _statusMessage.value = "Exported MIDI: ${file.name}"
                }
                .onFailure {
                    _statusMessage.value = "Failed to export MIDI: ${it.message}"
                }
        }
    }

    fun shareScore(context: Context) {
        val currentScore = _score.value ?: return
        val midiFile = _exportedMidiFile.value

        if (midiFile != null && midiFile.exists()) {
            shareFile(context, midiFile, "audio/midi", "Share MIDI File")
        } else if (!currentScore.rawAsciiContent.isNullOrBlank()) {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, currentScore.rawAsciiContent)
                putExtra(Intent.EXTRA_SUBJECT, currentScore.title)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share Tab Text")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
        } else {
            val dir = File(context.cacheDir, "shared_tabs")
            if (!dir.exists()) dir.mkdirs()
            val tempFile = File(dir, "${currentScore.title.replace(" ", "_")}.txt")
            tempFile.writeText("Title: ${currentScore.title}\nArtist: ${currentScore.artist}\nTempo: ${currentScore.tempo}\n")
            shareFile(context, tempFile, "text/plain", "Share Tab")
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        runCatching {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, title)
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        }
    }

    fun clearStatus() {
        _statusMessage.value = null
    }

    override fun onCleared() {
        playbackEngine.stop()
        super.onCleared()
    }

    private fun createSampleMeasures(): List<TabMeasure> {
        return listOf(
            TabMeasure(
                number = 1,
                beats = listOf(
                    TabBeat(notes = listOf(TabNote(0, 0), TabNote(5, 0)), startBeat = 0f),
                    TabBeat(notes = listOf(TabNote(1, 1)), startBeat = 0.5f),
                    TabBeat(notes = listOf(TabNote(2, 0)), startBeat = 1.0f),
                    TabBeat(notes = listOf(TabNote(3, 2)), startBeat = 1.5f),
                    TabBeat(notes = listOf(TabNote(4, 3)), startBeat = 2.0f),
                    TabBeat(notes = listOf(TabNote(3, 2)), startBeat = 2.5f),
                    TabBeat(notes = listOf(TabNote(2, 0)), startBeat = 3.0f),
                    TabBeat(notes = listOf(TabNote(1, 1)), startBeat = 3.5f),
                ),
            ),
            TabMeasure(
                number = 2,
                beats = listOf(
                    TabBeat(notes = listOf(TabNote(0, 3), TabNote(5, 3)), startBeat = 4.0f),
                    TabBeat(notes = listOf(TabNote(1, 0)), startBeat = 4.5f),
                    TabBeat(notes = listOf(TabNote(2, 0)), startBeat = 5.0f),
                    TabBeat(notes = listOf(TabNote(3, 0)), startBeat = 5.5f),
                    TabBeat(notes = listOf(TabNote(4, 2)), startBeat = 6.0f),
                    TabBeat(notes = listOf(TabNote(5, 3)), startBeat = 6.5f),
                ),
            ),
        )
    }

    private fun createSampleScore(): TabScore {
        val dropCTuningNotes = listOf("D4", "A3", "F3", "C3", "G2", "C2")
        val dropCLabels = listOf("d", "A", "F", "C", "G", "C")

        // Guitar 1 (Lead): 4 complete 4/4 measures (8 eighth notes = 4.0 beats each)
        val m1 = TabMeasure(
            number = 1,
            palmMute = true,
            palmMuteLabel = "P.M. ------------------------------------|",
            beats = listOf(
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 3, effect = NoteEffect.SLIDE)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 5)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )

        val m2 = TabMeasure(
            number = 2,
            palmMute = true,
            palmMuteLabel = "P.M. ------------------------------------|",
            beats = listOf(
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 7, effect = NoteEffect.VIBRATO)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 7)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 5)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 3)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )

        val m3 = TabMeasure(
            number = 3,
            palmMute = true,
            palmMuteLabel = "P.M. ------------------------------------|",
            beats = listOf(
                TabBeat(notes = listOf(TabNote(5, 8), TabNote(4, 8)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 8)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 8), TabNote(4, 8)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 8)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 7), TabNote(4, 7)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 7)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 5), TabNote(4, 5)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 5)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )

        val m4 = TabMeasure(
            number = 4,
            palmMute = false,
            beats = listOf(
                TabBeat(notes = listOf(TabNote(2, 7, effect = NoteEffect.BEND)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(2, 7)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(2, 5, effect = NoteEffect.VIBRATO)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(2, 5)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 7)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 7)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )

        val guitarTrack1 = TabTrack(
            name = "Overdriven Guitar / Guitar 1",
            instrumentType = InstrumentType.GUITAR,
            tuningName = "Drop C",
            stringLabels = dropCLabels,
            tuningNotes = dropCTuningNotes,
            measures = listOf(m1, m2, m3, m4),
            volume = 0.9f
        )

        // Guitar 2 (Rhythm): Heavy Drop C powerchords & chugs
        val g2m1 = TabMeasure(
            number = 1,
            palmMute = true,
            beats = listOf(
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0), TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 3), TabNote(4, 3)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 5), TabNote(4, 5)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )
        val g2m2 = TabMeasure(
            number = 2,
            palmMute = true,
            beats = listOf(
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 7), TabNote(4, 7)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 5), TabNote(4, 5)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )
        val g2m3 = TabMeasure(
            number = 3,
            palmMute = true,
            beats = listOf(
                TabBeat(notes = listOf(TabNote(5, 8), TabNote(4, 8)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 8)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 8), TabNote(4, 8)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 8)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 7), TabNote(4, 7)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 7)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 5), TabNote(4, 5)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 5)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )
        val g2m4 = TabMeasure(
            number = 4,
            palmMute = false,
            beats = listOf(
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 3), TabNote(4, 3)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 3)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 2), TabNote(4, 2)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 2)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(5, 0), TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )

        val guitarTrack2 = TabTrack(
            name = "Rhythm Guitar / Guitar 2",
            instrumentType = InstrumentType.GUITAR,
            tuningName = "Drop C",
            stringLabels = dropCLabels,
            tuningNotes = dropCTuningNotes,
            measures = listOf(g2m1, g2m2, g2m3, g2m4),
            volume = 0.85f
        )

        // Bass: 4 complete measures in Drop C (string 3 = low C)
        val bm1 = TabMeasure(
            number = 1,
            beats = listOf(
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 3)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 5)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )
        val bm2 = TabMeasure(
            number = 2,
            beats = listOf(
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 7)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 7)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 5)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 3)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )
        val bm3 = TabMeasure(
            number = 3,
            beats = listOf(
                TabBeat(notes = listOf(TabNote(3, 8)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 8)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 8)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 8)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 7)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 7)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 5)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 5)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )
        val bm4 = TabMeasure(
            number = 4,
            beats = listOf(
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 3)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 3)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 2)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 2)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )

        val bassTrack = TabTrack(
            name = "Bass Guitar",
            instrumentType = InstrumentType.BASS,
            tuningName = "Drop C (Bass)",
            stringCount = 4,
            stringLabels = listOf("F", "C", "G", "C"),
            tuningNotes = listOf("F2", "C2", "G1", "C1"),
            measures = listOf(bm1, bm2, bm3, bm4),
            volume = 0.95f
        )

        // Drumkit: 4 complete rock groove measures (8 eighth-note beats per measure)
        // 0: Crash, 1: Hi-Hat, 2: Snare, 3: Tom, 4: Bass Drum (Kick)
        val dm1 = TabMeasure(
            number = 1,
            beats = listOf(
                TabBeat(notes = listOf(TabNote(4, 0), TabNote(0, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(2, 0), TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(4, 0), TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(4, 0), TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(2, 0), TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )
        val dm2 = TabMeasure(
            number = 2,
            beats = listOf(
                TabBeat(notes = listOf(TabNote(4, 0), TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(2, 0), TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(4, 0), TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(4, 0), TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(2, 0), TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )
        val dm3 = TabMeasure(
            number = 3,
            beats = listOf(
                TabBeat(notes = listOf(TabNote(4, 0), TabNote(0, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(2, 0), TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(4, 0), TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(2, 0), TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(2, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )
        val dm4 = TabMeasure(
            number = 4,
            beats = listOf(
                TabBeat(notes = listOf(TabNote(4, 0), TabNote(0, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 0.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(2, 0), TabNote(1, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 1.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(3, 0)), durationType = NoteDuration.EIGHTH, startBeat = 2.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(2, 0)), durationType = NoteDuration.EIGHTH, startBeat = 2.5f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(4, 0), TabNote(0, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.0f, durationBeats = 0.5f),
                TabBeat(notes = listOf(TabNote(4, 0)), durationType = NoteDuration.EIGHTH, startBeat = 3.5f, durationBeats = 0.5f),
            )
        )

        val drumTrack = TabTrack(
            name = "Drumkit",
            instrumentType = InstrumentType.DRUMS,
            tuningName = "Standard Percussion",
            stringCount = 5,
            stringLabels = listOf("CC", "HH", "SD", "TM", "BD"),
            measures = listOf(dm1, dm2, dm3, dm4),
            volume = 1.0f
        )

        return TabScore(
            title = "Dark Clouds",
            artist = "Adept",
            revisionDate = "26.06.2018",
            tempo = 140,
            tracks = listOf(guitarTrack1, guitarTrack2, bassTrack, drumTrack)
        )
    }
}
