package com.mmt.guitarlab.audio.drums

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.mmt.guitarlab.audio.AudioFocusHandler
import com.mmt.guitarlab.domain.audio.DrumEngine
import com.mmt.guitarlab.domain.model.DrumKit
import com.mmt.guitarlab.domain.model.DrumPattern
import com.mmt.guitarlab.domain.model.DrumSound
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class AudioTrackDrumEngine @Inject constructor(
    private val audioFocus: AudioFocusHandler,
    @ApplicationContext private val context: Context,
) : DrumEngine {

    private val audioExecutor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "guitarlab-drums").apply { priority = Thread.MAX_PRIORITY }
    }
    private val scope = CoroutineScope(SupervisorJob() + audioExecutor.asCoroutineDispatcher())

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentStep = MutableStateFlow(-1)
    override val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _bpm = MutableStateFlow(110)
    override val bpm: StateFlow<Int> = _bpm.asStateFlow()

    private val _volume = MutableStateFlow(0.85f)
    override val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _swing = MutableStateFlow(0f)
    override val swing: StateFlow<Float> = _swing.asStateFlow()

    private val _pattern = MutableStateFlow(DrumPattern.DEFAULT_PATTERNS.first())
    override val pattern: StateFlow<DrumPattern> = _pattern.asStateFlow()

    private val _drumKit = MutableStateFlow(DrumKit.ROCK)
    override val drumKit: StateFlow<DrumKit> = _drumKit.asStateFlow()

    private val running = AtomicBoolean(false)
    private var audioTrack: AudioTrack? = null

    private data class StepWindow(
        val stepIndex: Int,
        val startFrame: Long,
        val endFrame: Long,
    )

    private val scheduledSteps = ConcurrentLinkedQueue<StepWindow>()

    override fun start() {
        if (running.getAndSet(true)) return
        _isPlaying.value = true
        _currentStep.value = 0
        scheduledSteps.clear()
        audioFocus.request()
        scope.launch { runLoop() }
    }

    override fun stop() {
        running.set(false)
        _isPlaying.value = false
        _currentStep.value = -1
        scheduledSteps.clear()
        audioFocus.abandon()
        try {
            audioTrack?.apply {
                pause()
                flush()
            }
        } catch (_: Throwable) {}
    }

    override fun setBpm(bpm: Int) {
        _bpm.value = bpm.coerceIn(30, 300)
    }

    override fun setVolume(volume: Float) {
        _volume.value = volume.coerceIn(0f, 1f)
    }

    override fun setSwing(swing: Float) {
        _swing.value = swing.coerceIn(-0.4f, 0.6f)
    }

    override fun setPattern(pattern: DrumPattern) {
        _pattern.value = pattern
        _bpm.value = pattern.defaultBpm
    }

    override fun setDrumKit(kit: DrumKit) {
        _drumKit.value = kit
    }

    override fun toggleStep(sound: DrumSound, step: Int) {
        if (step !in 0..15) return
        val current = _pattern.value
        val newGrid = current.grid.toMutableMap()
        val arr = newGrid[sound]?.clone() ?: BooleanArray(16) { false }
        arr[step] = !arr[step]
        newGrid[sound] = arr
        _pattern.value = current.copy(grid = newGrid)
    }

    override fun clearPattern() {
        val current = _pattern.value
        val emptyGrid = DrumSound.entries.associateWith { BooleanArray(16) { false } }
        _pattern.value = current.copy(name = "Custom Pattern", grid = emptyGrid)
    }

    override fun randomizePattern() {
        val current = _pattern.value
        val newGrid = mutableMapOf<DrumSound, BooleanArray>()
        DrumSound.entries.forEach { sound ->
            val arr = BooleanArray(16) { false }
            val prob = when (sound) {
                DrumSound.KICK -> 0.35
                DrumSound.SNARE -> 0.25
                DrumSound.HIHAT_CLOSED -> 0.55
                DrumSound.HIHAT_OPEN -> 0.15
                DrumSound.TOM_LOW, DrumSound.TOM_HIGH -> 0.12
                DrumSound.CRASH -> 0.08
                DrumSound.RIDE -> 0.30
            }
            for (i in 0 until 16) {
                if (sound == DrumSound.KICK && i == 0) {
                    arr[i] = true
                } else if (sound == DrumSound.SNARE && (i == 4 || i == 12)) {
                    arr[i] = true
                } else {
                    arr[i] = Math.random() < prob
                }
            }
            newGrid[sound] = arr
        }
        _pattern.value = current.copy(name = "Random Groove", grid = newGrid)
    }

    override fun resetPattern() {
        val currentId = _pattern.value.id
        val original = DrumPattern.DEFAULT_PATTERNS.find { it.id == currentId }
            ?: DrumPattern.DEFAULT_PATTERNS.first()
        _pattern.value = original
        _bpm.value = original.defaultBpm
    }

    override fun previewSound(sound: DrumSound) {
        val currentKit = _drumKit.value
        scope.launch {
            try {
                val sample = DrumSoundSynthesizer.getSample(sound, currentKit)
                val track = AudioTrack(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                    AudioFormat.Builder()
                        .setSampleRate(DrumSoundSynthesizer.SAMPLE_RATE)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build(),
                    sample.size * 2,
                    AudioTrack.MODE_STATIC,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
                )
                track.write(sample, 0, sample.size)
                track.play()
            } catch (_: Throwable) {}
        }
    }

    private suspend fun runLoop() {
        val sampleRate = DrumSoundSynthesizer.SAMPLE_RATE
        val minBuf = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSizeBytes = minBuf.coerceAtLeast(sampleRate / 2)

        val track = AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),
            AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build(),
            bufferSizeBytes,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
        audioTrack = track
        track.play()

        val clockJob = scope.launch {
            while (running.get() && isActive) {
                try {
                    val head = track.playbackHeadPosition.toLong() and 0xFFFFFFFFL
                    var matchedStep = -1
                    val it = scheduledSteps.iterator()
                    while (it.hasNext()) {
                        val sw = it.next()
                        if (head >= sw.endFrame) {
                            it.remove()
                        } else if (head in sw.startFrame until sw.endFrame) {
                            matchedStep = sw.stepIndex
                            break
                        }
                    }
                    if (matchedStep != -1 && matchedStep != _currentStep.value) {
                        _currentStep.value = matchedStep
                    }
                } catch (_: Throwable) {}
                delay(8)
            }
        }

        var step = 0
        var totalFramesWritten = 0L

        try {
            while (running.get() && scope.isActive) {
                val currentBpm = _bpm.value
                val vol = _volume.value
                val swingVal = _swing.value
                val currentPat = _pattern.value
                val currentKit = _drumKit.value

                // 16th note base duration in seconds
                val sixteenthSec = (60.0 / currentBpm) / 4.0

                // Correct Swing Math: Even 16ths (steps 0, 2, 4...) are elongated, odd 16ths (steps 1, 3, 5...) are shortened.
                // Pair duration stays exactly 2 * sixteenthSec so timing remains 100% steady!
                val durationSec = if (step % 2 == 0) {
                    sixteenthSec * (1.0 + swingVal.toDouble())
                } else {
                    sixteenthSec * (1.0 - swingVal.toDouble())
                }.coerceAtLeast(0.015)

                val stepSampleCount = (sampleRate * durationSec).roundToInt()

                val stepStartFrame = totalFramesWritten
                val stepEndFrame = totalFramesWritten + stepSampleCount
                scheduledSteps.add(StepWindow(step, stepStartFrame, stepEndFrame))
                totalFramesWritten += stepSampleCount

                while (running.get() && scope.isActive) {
                    val head = track.playbackHeadPosition.toLong() and 0xFFFFFFFFL
                    val queuedFrames = totalFramesWritten - head
                    if (queuedFrames < (sampleRate * 0.35)) {
                        break
                    }
                    delay(10)
                }

                val activeSounds = DrumSound.entries.filter { sound ->
                    currentPat.grid[sound]?.getOrNull(step) == true
                }

                val buffer = ShortArray(stepSampleCount)
                if (activeSounds.isNotEmpty()) {
                    val mix = DoubleArray(stepSampleCount)
                    // Accent downbeats slightly (steps 0, 4, 8, 12) for punchy human groove
                    val accentMultiplier = if (step % 4 == 0) 1.12 else 1.0

                    activeSounds.forEach { sound ->
                        val sample = DrumSoundSynthesizer.getSample(sound, currentKit)
                        val len = minOf(sample.size, stepSampleCount)
                        for (i in 0 until len) {
                            mix[i] += sample[i].toDouble() * accentMultiplier
                        }
                    }
                    for (i in 0 until stepSampleCount) {
                        val sampleVal = (mix[i] * vol).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        buffer[i] = sampleVal.toShort()
                    }
                }

                track.write(buffer, 0, buffer.size)

                step = (step + 1) % 16
            }
        } finally {
            clockJob.cancel()
            try {
                track.stop()
                track.release()
            } catch (_: Throwable) {}
            if (audioTrack === track) audioTrack = null
        }
    }
}
