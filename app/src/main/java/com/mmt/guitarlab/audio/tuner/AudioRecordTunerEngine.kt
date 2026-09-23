package com.mmt.guitarlab.audio.tuner

import android.annotation.SuppressLint
import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Process
import androidx.annotation.RequiresPermission
import com.mmt.guitarlab.domain.audio.TunerEngine
import com.mmt.guitarlab.domain.model.DetectedPitch
import com.mmt.guitarlab.domain.model.PitchMath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRecordTunerEngine @Inject constructor() : TunerEngine {

    private val audioThread = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "guitarlab-tuner").apply {
            priority = Thread.MAX_PRIORITY
        }
    }
    private val scope = CoroutineScope(SupervisorJob() + audioThread.asCoroutineDispatcher())

    private val _pitch = MutableStateFlow<DetectedPitch?>(null)
    override val pitch: StateFlow<DetectedPitch?> = _pitch.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    override val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    @Volatile
    private var a4Hz = 440f
    private val running = AtomicBoolean(false)
    private var job: Job? = null

    override fun setA4(hz: Float) {
        a4Hz = hz.coerceIn(415f, 466f)
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun start() {
        if (!running.compareAndSet(false, true)) return
        _isRunning.value = true
        job = scope.launch {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
            runCaptureLoop()
        }
    }

    override fun stop() {
        running.set(false)
        job?.cancel()
        job = null
        _isRunning.value = false
        _pitch.value = null
    }

    fun release() {
        stop()
        scope.cancel()
        audioThread.shutdown()
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun runCaptureLoop() {
        val sampleRate = pickSampleRate()
        val minBuf = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        if (minBuf <= 0) {
            running.set(false)
            _isRunning.value = false
            return
        }
        val readSize = maxOf(minBuf, FRAME_SIZE)
        val recorder = try {
            AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                readSize * 2,
            )
        } catch (_: SecurityException) {
            running.set(false)
            _isRunning.value = false
            return
        }
        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            recorder.release()
            running.set(false)
            _isRunning.value = false
            return
        }

        val yin = YinPitchDetector(sampleRate, FRAME_SIZE)
        val pcm = ShortArray(FRAME_SIZE)
        val floats = FloatArray(FRAME_SIZE)
        recorder.startRecording()
        try {
            while (running.get() && scope.isActive) {
                val n = recorder.read(pcm, 0, FRAME_SIZE)
                if (n < FRAME_SIZE) continue
                for (i in 0 until FRAME_SIZE) {
                    floats[i] = pcm[i] / 32768f
                }
                if (rms(floats) < RMS_GATE) {
                    _pitch.value = null
                    continue
                }
                val (hz, clarity) = yin.detect(floats)
                if (hz in 20f..1500f && clarity > 0.65f) {
                    _pitch.value = PitchMath.fromFrequency(hz, a4Hz, clarity)
                } else {
                    _pitch.value = null
                }
            }
        } finally {
            runCatching { recorder.stop() }
            recorder.release()
            _isRunning.value = false
            running.set(false)
        }
    }

    private fun rms(samples: FloatArray): Float {
        var sum = 0f
        for (s in samples) sum += s * s
        return kotlin.math.sqrt(sum / samples.size)
    }

    private fun pickSampleRate(): Int {
        val candidates = intArrayOf(44_100, 48_000, 22_050)
        for (rate in candidates) {
            val size = AudioRecord.getMinBufferSize(
                rate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
            )
            if (size > 0) return rate
        }
        return 44_100
    }

    companion object {
        private const val FRAME_SIZE = 4096
        private const val RMS_GATE = 0.008f
    }
}
