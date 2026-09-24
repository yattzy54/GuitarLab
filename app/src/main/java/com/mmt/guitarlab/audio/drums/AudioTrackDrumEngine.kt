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
        _swing.value = swing.coerceIn(-0.35f, 0.35f)
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
        // Set buffer size to ~200-250ms for smooth non-blocking playback with low buffer lag
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

        // Background coroutine that continuously synchronizes _currentStep with track playback head
        val clockJob = scope.launch {
            while (running.get() && isActive) {
                try {
                    val head = track.playbackHeadPosition.toLong() and 0xFFFFFFFFL
                    var matchedStep = -1
                    val it = scheduledSteps.iterator()
                    while (it.hasNext()) {
                        val sw = it.next()
                        if (head >= sw.endFrame) {
                            it.remove() // step has already passed through speaker
                        } else if (head in sw.startFrame until sw.endFrame) {
                            matchedStep = sw.stepIndex
                            break
                        }
                    }
                    if (matchedStep != -1 && matchedStep != _currentStep.value) {
                        _currentStep.value = matchedStep
                    }
                } catch (_: Throwable) {}
                delay(8) // Check at 120 FPS so no step is ever skipped
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
                val swingOffset = if (step % 2 == 1) sixteenthSec * swingVal else -sixteenthSec * swingVal
                val durationSec = (sixteenthSec + swingOffset).coerceAtLeast(0.02)
                val stepSampleCount = (sampleRate * durationSec).roundToInt()

                val stepStartFrame = totalFramesWritten
                val stepEndFrame = totalFramesWritten + stepSampleCount
                scheduledSteps.add(StepWindow(step, stepStartFrame, stepEndFrame))
                totalFramesWritten += stepSampleCount

                // If writer has written more than 350ms ahead of the speaker, throttle slightly
                while (running.get() && scope.isActive) {
                    val head = track.playbackHeadPosition.toLong() and 0xFFFFFFFFL
                    val queuedFrames = totalFramesWritten - head
                    if (queuedFrames < (sampleRate * 0.35)) {
                        break
                    }
                    delay(10)
                }

                // Gather active sounds for this step
                val activeSounds = DrumSound.entries.filter { sound ->
                    currentPat.grid[sound]?.getOrNull(step) == true
                }

                val buffer = ShortArray(stepSampleCount)
                if (activeSounds.isNotEmpty()) {
                    val mix = DoubleArray(stepSampleCount)
                    activeSounds.forEach { sound ->
                        val sample = DrumSoundSynthesizer.getSample(sound, currentKit)
                        val len = minOf(sample.size, stepSampleCount)
                        for (i in 0 until len) {
                            mix[i] += sample[i].toDouble()
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
