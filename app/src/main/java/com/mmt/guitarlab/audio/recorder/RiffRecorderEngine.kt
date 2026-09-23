package com.mmt.guitarlab.audio.recorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiffRecorderEngine @Inject constructor() {

    private var recorder: MediaRecorder? = null
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDurationMs = MutableStateFlow(0L)
    val recordingDurationMs: StateFlow<Long> = _recordingDurationMs.asStateFlow()

    private var currentOutputFile: File? = null

    fun startRecording(context: Context, title: String): File? {
        val dir = File(context.getExternalFilesDir(null), "riff_recordings")
        if (!dir.exists()) dir.mkdirs()

        val fileName = "Riff_${System.currentTimeMillis()}.m4a"
        val outputFile = File(dir, fileName)
        currentOutputFile = outputFile

        runCatching {
            val mr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mr.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(128000)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            recorder = mr
            _isRecording.value = true
            _recordingDurationMs.value = 0L

            startTimer()
            return outputFile
        }.onFailure {
            releaseRecorder()
            return null
        }
        return null
    }

    fun stopRecording(): File? {
        timerJob?.cancel()
        val file = currentOutputFile
        runCatching {
            recorder?.stop()
        }
        releaseRecorder()
        _isRecording.value = false
        return file
    }

    private fun startTimer() {
        timerJob?.cancel()
        val startTime = System.currentTimeMillis()
        timerJob = scope.launch {
            while (isActive) {
                _recordingDurationMs.value = System.currentTimeMillis() - startTime
                delay(200)
            }
        }
    }

    private fun releaseRecorder() {
        runCatching { recorder?.release() }
        recorder = null
        _isRecording.value = false
    }
}
