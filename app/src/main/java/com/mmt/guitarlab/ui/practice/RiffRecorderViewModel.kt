package com.mmt.guitarlab.ui.practice

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmt.guitarlab.audio.recorder.RiffRecorderEngine
import com.mmt.guitarlab.data.db.RiffRecordDao
import com.mmt.guitarlab.data.db.RiffRecordEntity
import com.mmt.guitarlab.domain.repository.TuningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RiffRecorderViewModel @Inject constructor(
    private val recorderEngine: RiffRecorderEngine,
    private val riffDao: RiffRecordDao,
    private val tuningRepository: TuningRepository,
) : ViewModel() {

    val isRecording: StateFlow<Boolean> = recorderEngine.isRecording
    val recordingDurationMs: StateFlow<Long> = recorderEngine.recordingDurationMs

    val riffList: StateFlow<List<RiffRecordEntity>> = riffDao.getAllRiffs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val activeTuningName: StateFlow<String> = combine(
        tuningRepository.getTunings(),
        tuningRepository.getSelectedTuningId(),
    ) { list, id ->
        list.find { it.id == id }?.name ?: "Standard E"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Standard E")

    private val _playingRiffId = MutableStateFlow<Long?>(null)
    val playingRiffId: StateFlow<Long?> = _playingRiffId.asStateFlow()

    private var player: MediaPlayer? = null

    fun startRecording(context: Context, title: String) {
        recorderEngine.startRecording(context, title)
    }

    fun stopRecording(title: String, bpm: Int) {
        val duration = recordingDurationMs.value
        val file = recorderEngine.stopRecording()
        if (file != null && file.exists()) {
            viewModelScope.launch {
                val entity = RiffRecordEntity(
                    filePath = file.absolutePath,
                    title = title.ifBlank { "Riff Memo" },
                    timestamp = System.currentTimeMillis(),
                    durationMs = duration,
                    bpm = bpm,
                    tuningName = activeTuningName.value,
                )
                riffDao.insertRiff(entity)
            }
        }
    }

    fun playRiff(riff: RiffRecordEntity) {
        stopPlayer()
        if (_playingRiffId.value == riff.id) {
            _playingRiffId.value = null
            return
        }

        runCatching {
            val mp = MediaPlayer()
            mp.setDataSource(riff.filePath)
            mp.prepare()
            mp.setOnCompletionListener {
                _playingRiffId.value = null
            }
            mp.start()
            player = mp
            _playingRiffId.value = riff.id
        }
    }

    fun stopPlayer() {
        runCatching {
            player?.stop()
            player?.release()
        }
        player = null
        _playingRiffId.value = null
    }

    fun deleteRiff(riff: RiffRecordEntity) {
        if (_playingRiffId.value == riff.id) stopPlayer()
        viewModelScope.launch {
            runCatching { File(riff.filePath).delete() }
            riffDao.deleteRiff(riff)
        }
    }

    override fun onCleared() {
        stopPlayer()
        super.onCleared()
    }
}
