package com.mmt.guitarlab.audio.tuner

import android.Manifest
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
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
import kotlin.math.abs
import kotlin.math.sqrt

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

        val recorder = createAudioRecord(sampleRate, minBuf) ?: run {
            running.set(false)
            _isRunning.value = false
            return
        }

        val yin = YinPitchDetector(sampleRate, FRAME_SIZE)
        val hopSize = 1024
        val pcmChunk = ShortArray(hopSize)
        val slidingBuffer = FloatArray(FRAME_SIZE)
        val normalizedBuffer = FloatArray(FRAME_SIZE)

        var lastValidPitch: DetectedPitch? = null
        var lastValidTime = 0L

        recorder.startRecording()
        try {
            while (running.get() && scope.isActive) {
                val n = recorder.read(pcmChunk, 0, hopSize)
                if (n < hopSize) continue

                // Shift sliding buffer left by hopSize and insert new samples at end
                System.arraycopy(slidingBuffer, hopSize, slidingBuffer, 0, FRAME_SIZE - hopSize)
                val baseIdx = FRAME_SIZE - hopSize
                for (i in 0 until hopSize) {
                    slidingBuffer[baseIdx + i] = pcmChunk[i] / 32768f
                }

                // Compute RMS and Peak for silence detection and pre-amplification
                var sumSq = 0f
                var maxAbs = 0f
                for (i in 0 until FRAME_SIZE) {
                    val s = slidingBuffer[i]
                    sumSq += s * s
                    val a = abs(s)
                    if (a > maxAbs) maxAbs = a
                }
                val currentRms = sqrt(sumSq / FRAME_SIZE)

                // High-sensitivity gate for quiet unplugged electric guitars
                if (currentRms < RMS_GATE || maxAbs < 0.0003f) {
                    val now = System.currentTimeMillis()
                    if (now - lastValidTime > SUSTAIN_HOLD_MS) {
                        _pitch.value = null
                    }
                    continue
                }

                // Adaptive gain boosting: boosts quiet guitar signals into optimal range
                val gain = (0.75f / maxAbs).coerceIn(1.0f, 80.0f)
                for (i in 0 until FRAME_SIZE) {
                    normalizedBuffer[i] = (slidingBuffer[i] * gain).coerceIn(-1.0f, 1.0f)
                }

                val (hz, clarity) = yin.detect(normalizedBuffer)
                val now = System.currentTimeMillis()

                // Wide range (30Hz for Low B bass / drop tunings to 1500Hz high frets)
                // Relaxed clarity threshold (0.42f) for rich electric harmonics & decay
                if (hz in 30f..1500f && clarity >= 0.42f) {
                    val rawPitch = PitchMath.fromFrequency(hz, a4Hz, clarity)
                    if (rawPitch != null) {
                        // Temporal smoothing for rock-solid needle stability
                        val smoothedCents = if (lastValidPitch != null && lastValidPitch.midiNote == rawPitch.midiNote) {
                            lastValidPitch.cents * 0.70f + rawPitch.cents * 0.30f
                        } else {
                            rawPitch.cents
                        }

                        val stablePitch = rawPitch.copy(cents = smoothedCents)
                        lastValidPitch = stablePitch
                        lastValidTime = now
                        _pitch.value = stablePitch
                    }
                } else {
                    if (now - lastValidTime > SUSTAIN_HOLD_MS) {
                        _pitch.value = null
                    }
                }
            }
        } finally {
            runCatching { recorder.stop() }
            recorder.release()
            _isRunning.value = false
            running.set(false)
        }
    }

    @SuppressLint("MissingPermission")
    private fun createAudioRecord(sampleRate: Int, minBuf: Int): AudioRecord? {
        val readSize = maxOf(minBuf, FRAME_SIZE)

        // 1. Try UNPROCESSED (Android 7.0+ API 24): Raw sound without voice filters
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                val record = AudioRecord(
                    MediaRecorder.AudioSource.UNPROCESSED,
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    readSize * 2,
                )
                if (record.state == AudioRecord.STATE_INITIALIZED) {
                    return record
                } else {
                    record.release()
                }
            } catch (_: Throwable) {
                // Fallback to MIC
            }
        }

        // 2. Standard MIC: raw microphone audio without speech high-pass filter
        try {
            val record = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                readSize * 2,
            )
            if (record.state == AudioRecord.STATE_INITIALIZED) {
                return record
            } else {
                record.release()
            }
        } catch (_: Throwable) {
            // Fallback to DEFAULT
        }

        // 3. Fallback to DEFAULT
        return try {
            val record = AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                readSize * 2,
            )
            if (record.state == AudioRecord.STATE_INITIALIZED) record else {
                record.release()
                null
            }
        } catch (_: Throwable) {
            null
        }
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
        // Ultra-sensitive RMS gate for quiet unplugged electric guitar (~0.0004f)
        private const val RMS_GATE = 0.0004f
        // Keep needle stable for 320ms during string vibration decay
        private const val SUSTAIN_HOLD_MS = 320L
    }
}
