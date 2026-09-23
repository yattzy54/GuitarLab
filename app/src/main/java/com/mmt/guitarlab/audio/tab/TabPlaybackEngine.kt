package com.mmt.guitarlab.audio.tab

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.TabNote
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.sin

@Singleton
class TabPlaybackEngine @Inject constructor() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var playbackJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentMeasureIndex = MutableStateFlow(0)
    val currentMeasureIndex: StateFlow<Int> = _currentMeasureIndex.asStateFlow()

    private val _currentBeatIndex = MutableStateFlow(0)
    val currentBeatIndex: StateFlow<Int> = _currentBeatIndex.asStateFlow()

    private val _speedMultiplier = MutableStateFlow(1.0f)
    val speedMultiplier: StateFlow<Float> = _speedMultiplier.asStateFlow()

    private val random = Random()

    fun play(score: TabScore, activeTrackIndex: Int = 0, startMeasureIndex: Int = 0) {
        stop()
        _isPlaying.value = true

        playbackJob = scope.launch {
            val soloTracks = score.tracks.filter { it.isSolo }
            val playableTracks = if (soloTracks.isNotEmpty()) soloTracks else score.tracks.filter { !it.isMuted }
            if (playableTracks.isEmpty()) return@launch

            val primaryTrack = score.tracks.getOrNull(activeTrackIndex) ?: playableTracks.first()
            val totalMeasures = primaryTrack.measures.size
            if (totalMeasures == 0) return@launch

            val tempo = score.tempo.coerceIn(30, 300)

            for (mIdx in startMeasureIndex until totalMeasures) {
                if (!isActive) break
                _currentMeasureIndex.value = mIdx

                val maxBeatsInMeasure = playableTracks.maxOfOrNull {
                    it.measures.getOrNull(mIdx)?.beats?.size ?: 0
                } ?: 0

                for (bIdx in 0 until maxBeatsInMeasure) {
                    if (!isActive) break
                    _currentBeatIndex.value = bIdx

                    // Play notes across all playable tracks concurrently
                    playableTracks.forEach { track ->
                        val beat = track.measures.getOrNull(mIdx)?.beats?.getOrNull(bIdx)
                        if (beat != null) {
                            playTrackBeatNotes(track, beat.notes)
                        }
                    }

                    val speed = _speedMultiplier.value.coerceIn(0.25f, 2.0f)
                    val beatDurationMs = ((60_000f / tempo) * 0.5f / speed).toLong()
                    delay(beatDurationMs.coerceAtLeast(100L))
                }
            }

            _isPlaying.value = false
            _currentMeasureIndex.value = 0
            _currentBeatIndex.value = 0
        }
    }

    fun pause() {
        playbackJob?.cancel()
        playbackJob = null
        _isPlaying.value = false
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        _isPlaying.value = false
        _currentMeasureIndex.value = 0
        _currentBeatIndex.value = 0
    }

    fun setSpeed(speed: Float) {
        _speedMultiplier.value = speed.coerceIn(0.25f, 2.0f)
    }

    private fun playTrackBeatNotes(track: TabTrack, notes: List<TabNote>) {
        if (notes.isEmpty()) return
        scope.launch {
            notes.forEach { note ->
                when (track.instrumentType) {
                    InstrumentType.DRUMS -> playDrumHit(note.stringIndex)
                    InstrumentType.BASS -> {
                        val base = floatArrayOf(98.00f, 73.42f, 55.00f, 41.20f)
                        val freq = base.getOrElse(note.stringIndex) { 41.20f } * 2.0f.pow(note.fret / 12.0f)
                        playSynthesizedTone(freq, durationMs = 350, isBass = true)
                    }
                    InstrumentType.UKULELE -> {
                        val base = floatArrayOf(440.00f, 329.63f, 261.63f, 392.00f)
                        val freq = base.getOrElse(note.stringIndex) { 261.63f } * 2.0f.pow(note.fret / 12.0f)
                        playSynthesizedTone(freq, durationMs = 200, isBass = false)
                    }
                    InstrumentType.KEYBOARD -> {
                        val base = floatArrayOf(523.25f, 392.00f, 329.63f, 261.63f, 196.00f, 130.81f)
                        val freq = base.getOrElse(note.stringIndex) { 261.63f } * 2.0f.pow(note.fret / 12.0f)
                        playSynthesizedTone(freq, durationMs = 400, isBass = false)
                    }
                    InstrumentType.GUITAR -> {
                        val base = floatArrayOf(329.63f, 246.94f, 196.00f, 146.83f, 110.00f, 82.41f)
                        val freq = base.getOrElse(note.stringIndex) { 110.00f } * 2.0f.pow(note.fret / 12.0f)
                        playSynthesizedTone(freq, durationMs = 280, isBass = false)
                    }
                }
            }
        }
    }

    private fun playDrumHit(drumIndex: Int) {
        runCatching {
            val sampleRate = 22050
            val durationMs = 150
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            val samples = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val decay = (1.0 - i.toDouble() / numSamples).pow(2.0)

                val valInt = when (drumIndex) {
                    0 -> { // Crash
                        val noise = (random.nextDouble() * 2.0 - 1.0) * 0.6
                        val tone = sin(2.0 * Math.PI * 800.0 * t) * 0.4
                        ((noise + tone) * 32767 * decay).toInt()
                    }
                    1 -> { // Hi-Hat
                        val noise = (random.nextDouble() * 2.0 - 1.0) * 0.8
                        (noise * 32767 * (1.0 - i.toDouble() / numSamples).pow(4.0)).toInt()
                    }
                    2 -> { // Snare
                        val noise = (random.nextDouble() * 2.0 - 1.0) * 0.5
                        val body = sin(2.0 * Math.PI * 180.0 * t) * 0.5
                        ((noise + body) * 32767 * decay).toInt()
                    }
                    3 -> { // Tom
                        val freq = 120.0 * (1.0 - t * 2.0).coerceAtLeast(0.5)
                        val body = sin(2.0 * Math.PI * freq * t)
                        (body * 32767 * decay).toInt()
                    }
                    else -> { // Kick
                        val freq = 80.0 * (1.0 - t * 4.0).coerceAtLeast(0.3)
                        val body = sin(2.0 * Math.PI * freq * t)
                        (body * 32767 * (1.0 - i.toDouble() / numSamples).pow(1.2)).toInt()
                    }
                }
                samples[i] = valInt.coerceIn(-32768, 32767).toShort()
            }

            playAudioTrack(samples)
        }
    }

    private fun playSynthesizedTone(frequencyHz: Float, durationMs: Int, isBass: Boolean) {
        runCatching {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            val samples = ShortArray(numSamples)

            val twoPiF = 2.0 * Math.PI * frequencyHz / sampleRate

            for (i in 0 until numSamples) {
                val decay = (1.0 - i.toDouble() / numSamples).pow(if (isBass) 1.2 else 1.8)
                val fundamental = sin(twoPiF * i)
                val harmonic = if (!isBass) sin(twoPiF * i * 2.0) * 0.3 else 0.0
                val sample = ((fundamental + harmonic) * 32767 * decay * 0.45).toInt()
                samples[i] = sample.coerceIn(-32768, 32767).toShort()
            }

            playAudioTrack(samples)
        }
    }

    private fun playAudioTrack(samples: ShortArray) {
        val sampleRate = 22050
        val numSamples = samples.size
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            )
            .setBufferSizeInBytes(numSamples * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(samples, 0, numSamples)
        audioTrack.play()
        scope.launch {
            delay(200L + (numSamples * 1000L / sampleRate))
            audioTrack.release()
        }
    }
}
