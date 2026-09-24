package com.mmt.guitarlab.audio.metronome

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Process
import com.mmt.guitarlab.audio.AudioFocusHandler
import com.mmt.guitarlab.domain.audio.MetronomeEngine
import com.mmt.guitarlab.domain.model.MetronomeBeat
import com.mmt.guitarlab.domain.model.MetronomeConfig
import com.mmt.guitarlab.domain.model.TrainerIntervalKind
import kotlinx.coroutines.CoroutineScope
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
import android.content.Context
import android.os.Vibrator
import android.os.VibratorManager
import android.os.VibrationEffect
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class AudioTrackMetronomeEngine @Inject constructor(
    private val audioFocus: AudioFocusHandler,
    @ApplicationContext private val context: Context,
) : MetronomeEngine {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private val audioExecutor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "guitarlab-metronome").apply { priority = Thread.MAX_PRIORITY }
    }
    private val scope = CoroutineScope(SupervisorJob() + audioExecutor.asCoroutineDispatcher())

    private val _config = MutableStateFlow(MetronomeConfig())
    override val config: StateFlow<MetronomeConfig> = _config.asStateFlow()

    private val _beat = MutableStateFlow<MetronomeBeat?>(null)
    override val beat: StateFlow<MetronomeBeat?> = _beat.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    override val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val running = AtomicBoolean(false)

    override fun updateConfig(transform: (MetronomeConfig) -> MetronomeConfig) {
        _config.value = transform(_config.value).let { cfg ->
            cfg.copy(bpm = cfg.bpm.coerceIn(MetronomeConfig.MIN_BPM, MetronomeConfig.MAX_BPM))
        }
    }

    override fun start() {
        if (!running.compareAndSet(false, true)) return
        audioFocus.request()
        _isRunning.value = true
        scope.launch { runLoop() }
    }

    override fun stop() {
        running.set(false)
        _isRunning.value = false
        _beat.value = null
        audioFocus.abandon()
    }

    fun release() {
        stop()
        scope.cancel()
        audioExecutor.shutdown()
    }

    private fun runLoop() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
        val sampleRate = AudioTrack.getNativeOutputSampleRate(AudioTrack.MODE_STREAM)
            .takeIf { it > 0 } ?: 44_100
        val minBuf = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        val bufferFrames = maxOf(minBuf, sampleRate / 50)
        val track = buildTrack(sampleRate, bufferFrames)
        var currentSound = _config.value.sound
        var accentClick = ClickSynthesizer.generateClick(currentSound, sampleRate, accent = true)
        var weakClick = ClickSynthesizer.generateClick(currentSound, sampleRate, accent = false)
        val chunk = ShortArray(CHUNK)

        var beatInBar = 1
        var barIndex = 0L
        var samplesUntilClick = 0
        var clickPos = 0
        var currentClick: ShortArray? = null
        var bpm = _config.value.bpm
        var trainerAnchorMs = System.currentTimeMillis()
        var barsSinceJump = 0
        var beatsToJump = 0
        var jumpIntervalMs = 0L

        fun framesPerBeat(currentBpm: Int): Int {
            val safe = currentBpm.coerceIn(MetronomeConfig.MIN_BPM, MetronomeConfig.MAX_BPM)
            return (sampleRate * 60.0 / safe).roundToInt().coerceAtLeast(1)
        }

        fun refreshTrainer(cfg: MetronomeConfig) {
            val t = cfg.trainer
            if (!t.enabled) {
                beatsToJump = 0
                jumpIntervalMs = 0L
                return
            }
            when (t.intervalKind) {
                TrainerIntervalKind.BARS -> {
                    val remainingBars = (t.intervalValue - barsSinceJump).coerceAtLeast(1)
                    beatsToJump = remainingBars * cfg.timeSignature.beatsPerBar - (beatInBar - 1)
                }
                TrainerIntervalKind.MINUTES -> {
                    jumpIntervalMs = t.intervalValue * 60_000L
                    beatsToJump = 0
                }
            }
        }

        fun maybeIncreaseTempo(cfg: MetronomeConfig, nowMs: Long): Int {
            val t = cfg.trainer
            if (!t.enabled) return bpm
            val atTarget = bpm >= t.targetBpm && t.targetBpm >= t.startBpm ||
                bpm <= t.targetBpm && t.targetBpm < t.startBpm
            if (atTarget && bpm == t.targetBpm) return bpm
            val shouldJump = when (t.intervalKind) {
                TrainerIntervalKind.BARS -> barsSinceJump >= t.intervalValue
                TrainerIntervalKind.MINUTES -> nowMs - trainerAnchorMs >= t.intervalValue * 60_000L
            }
            if (!shouldJump) return bpm
            val direction = if (t.targetBpm >= t.startBpm) 1 else -1
            val next = (bpm + direction * t.incrementBpm).let { candidate ->
                if (direction > 0) minOf(candidate, t.targetBpm) else maxOf(candidate, t.targetBpm)
            }.coerceIn(MetronomeConfig.MIN_BPM, MetronomeConfig.MAX_BPM)
            barsSinceJump = 0
            trainerAnchorMs = nowMs
            updateConfig { it.copy(bpm = next) }
            return next
        }

        samplesUntilClick = 0
        track.play()
        try {
            while (running.get() && scope.isActive) {
                val cfg = _config.value
                bpm = cfg.bpm
                val vibrateOnly = cfg.vibrateOnly
                val volume = if (vibrateOnly) 0f else cfg.volume.coerceIn(0f, 1f)
                val ts = cfg.timeSignature

                if (cfg.sound != currentSound) {
                    currentSound = cfg.sound
                    accentClick = ClickSynthesizer.generateClick(currentSound, sampleRate, accent = true)
                    weakClick = ClickSynthesizer.generateClick(currentSound, sampleRate, accent = false)
                }

                refreshTrainer(cfg)

                var i = 0
                while (i < chunk.size) {
                    if (samplesUntilClick <= 0) {
                        val now = System.currentTimeMillis()
                        val accent = beatInBar in ts.accentBeats
                        currentClick = if (accent) accentClick else weakClick
                        clickPos = 0
                        val millisUntil = when {
                            cfg.trainer.enabled && cfg.trainer.intervalKind == TrainerIntervalKind.MINUTES ->
                                (jumpIntervalMs - (now - trainerAnchorMs)).coerceAtLeast(0L)
                            else -> null
                        }
                        val progress = when {
                            !cfg.trainer.enabled -> 0f
                            cfg.trainer.intervalKind == TrainerIntervalKind.BARS -> {
                                val total = (cfg.trainer.intervalValue * ts.beatsPerBar).coerceAtLeast(1)
                                val done = barsSinceJump * ts.beatsPerBar + (beatInBar - 1)
                                (done.toFloat() / total).coerceIn(0f, 1f)
                            }
                            else -> {
                                val elapsed = (now - trainerAnchorMs).toFloat()
                                (elapsed / jumpIntervalMs.coerceAtLeast(1)).coerceIn(0f, 1f)
                            }
                        }
                        _beat.value = MetronomeBeat(
                            beatInBar = beatInBar,
                            barIndex = barIndex,
                            accent = accent,
                            bpm = bpm,
                            progressToNextJump = progress,
                            beatsUntilJump = if (cfg.trainer.enabled &&
                                cfg.trainer.intervalKind == TrainerIntervalKind.BARS
                            ) beatsToJump else null,
                            millisUntilJump = millisUntil,
                        )

                        if (vibrateOnly && vibrator?.hasVibrator() == true) {
                            try {
                                val duration = if (accent) 32L else 18L
                                val amplitude = if (accent) VibrationEffect.DEFAULT_AMPLITUDE else 120
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator?.vibrate(VibrationEffect.createOneShot(duration, amplitude))
                                } else {
                                    @Suppress("DEPRECATION")
                                    vibrator?.vibrate(duration)
                                }
                            } catch (_: Exception) {}
                        }
                        if (beatInBar >= ts.beatsPerBar) {
                            beatInBar = 1
                            barIndex++
                            barsSinceJump++
                            bpm = maybeIncreaseTempo(cfg, now)
                        } else {
                            beatInBar++
                        }
                        samplesUntilClick = framesPerBeat(bpm)
                    }

                    var sample = 0
                    val click = currentClick
                    if (click != null && clickPos < click.size) {
                        sample = click[clickPos].toInt()
                        clickPos++
                    }
                    chunk[i] = (sample * volume).toInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        .toShort()
                    samplesUntilClick--
                    i++
                }
                track.write(chunk, 0, chunk.size)
            }
        } finally {
            runCatching { track.pause() }
            runCatching { track.flush() }
            track.release()
            _isRunning.value = false
            running.set(false)
        }
    }

    private fun buildTrack(sampleRate: Int, bufferFrames: Int): AudioTrack {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        val format = AudioFormat.Builder()
            .setSampleRate(sampleRate)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioTrack.Builder()
                .setAudioAttributes(attrs)
                .setAudioFormat(format)
                .setBufferSizeInBytes(bufferFrames)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                .build()
        } else {
            @Suppress("DEPRECATION")
            AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferFrames,
                AudioTrack.MODE_STREAM,
            )
        }
        return track
    }

    companion object {
        private const val CHUNK = 512
    }
}
