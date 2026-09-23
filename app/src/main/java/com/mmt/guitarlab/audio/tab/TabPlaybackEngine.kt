package com.mmt.guitarlab.audio.tab

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.NoteDuration
import com.mmt.guitarlab.domain.model.NoteEffect
import com.mmt.guitarlab.domain.model.TabNote
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI
import kotlin.math.exp
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

    private var loopStartMeasure: Int? = null
    private var loopEndMeasure: Int? = null

    private val random = Random()
    private val sampleRate = 44100

    fun play(
        score: TabScore,
        activeTrackIndex: Int = 0,
        startMeasureIndex: Int = 0,
        startBeatIndex: Int = 0,
    ) {
        stop()
        _isPlaying.value = true

        playbackJob = scope.launch(Dispatchers.Default) {
            val soloTracks = score.tracks.filter { it.isSolo }
            val playableTracks = if (soloTracks.isNotEmpty()) soloTracks else score.tracks.filter { !it.isMuted }
            if (playableTracks.isEmpty()) {
                _isPlaying.value = false
                return@launch
            }

            val primaryTrack = score.tracks.getOrNull(activeTrackIndex) ?: playableTracks.first()
            val totalMeasures = primaryTrack.measures.size
            if (totalMeasures == 0) {
                _isPlaying.value = false
                return@launch
            }

            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
            )
            val bufferSize = maxOf(minBuf, sampleRate / 4 * 2)

            val audioTrack: AudioTrack? = runCatching {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .build(),
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            }.getOrNull()

            audioTrack?.play()

            try {
                val tempo = score.tempo.coerceIn(30, 320)
                var currentM = startMeasureIndex.coerceIn(0, totalMeasures - 1)
                var currentB = startBeatIndex

                while (isActive && currentM < totalMeasures) {
                    // Check loop boundaries
                    val lStart = loopStartMeasure
                    val lEnd = loopEndMeasure
                    if (lStart != null && lEnd != null && (currentM > lEnd || currentM < lStart)) {
                        currentM = lStart
                        currentB = 0
                    }

                    _currentMeasureIndex.value = currentM

                    val maxBeatsInMeasure = playableTracks.maxOfOrNull {
                        it.measures.getOrNull(currentM)?.beats?.size ?: 0
                    } ?: 0

                    if (maxBeatsInMeasure == 0) {
                        currentM++
                        currentB = 0
                        continue
                    }

                    while (isActive && currentB < maxBeatsInMeasure) {
                        _currentBeatIndex.value = currentB

                        // Determine beat duration in beats
                        val representativeBeat = playableTracks.firstNotNullOfOrNull {
                            it.measures.getOrNull(currentM)?.beats?.getOrNull(currentB)
                        }
                        val durationBeats = representativeBeat?.durationBeats ?: when (representativeBeat?.durationType) {
                            NoteDuration.WHOLE -> 4.0f
                            NoteDuration.HALF -> 2.0f
                            NoteDuration.QUARTER -> 1.0f
                            NoteDuration.EIGHTH -> 0.5f
                            NoteDuration.SIXTEENTH -> 0.25f
                            else -> 0.5f
                        }

                        val speed = _speedMultiplier.value.coerceIn(0.25f, 2.5f)
                        val beatSeconds = (60.0 / (tempo * speed)) * durationBeats.coerceIn(0.125f, 4.0f)
                        val numSamples = (sampleRate * beatSeconds).toInt().coerceIn(1024, sampleRate * 3)

                        val mixBuffer = FloatArray(numSamples)

                        // Synthesize all notes in this beat across all playable tracks
                        playableTracks.forEach { track ->
                            val beat = track.measures.getOrNull(currentM)?.beats?.getOrNull(currentB)
                            if (beat != null && beat.notes.isNotEmpty()) {
                                val trackVol = track.volume.coerceIn(0f, 1f)
                                beat.notes.forEach { note ->
                                    synthesizeNoteIntoMix(track, note, mixBuffer, numSamples, trackVol)
                                }
                            }
                        }

                        // Convert mixed float buffer to 16-bit PCM with soft limiting
                        if (audioTrack != null) {
                            val pcm = ShortArray(numSamples)
                            for (i in 0 until numSamples) {
                                val s = mixBuffer[i]
                                val saturated = when {
                                    s > 1.0f -> 1.0f - 1.0f / (s + 1.0f)
                                    s < -1.0f -> -1.0f + 1.0f / (-s + 1.0f)
                                    else -> s
                                }
                                pcm[i] = (saturated * 31500f).toInt().toShort()
                            }
                            audioTrack.write(pcm, 0, numSamples)
                        } else {
                            // Fallback delay if AudioTrack cannot be built on device
                            kotlinx.coroutines.delay((beatSeconds * 1000).toLong())
                        }

                        currentB++
                    }

                    currentB = 0
                    currentM++

                    // Check if looped
                    if (lStart != null && lEnd != null && currentM > lEnd) {
                        currentM = lStart
                    }
                }
            } finally {
                runCatching {
                    audioTrack?.stop()
                    audioTrack?.release()
                }
                _isPlaying.value = false
            }
        }
    }

    private fun synthesizeNoteIntoMix(
        track: TabTrack,
        note: TabNote,
        mix: FloatArray,
        numSamples: Int,
        trackVol: Float,
    ) {
        val velocityGain = (note.velocity.coerceIn(20, 127) / 127f) * trackVol

        when (track.instrumentType) {
            InstrumentType.DRUMS -> {
                synthesizeDrumHit(note.stringIndex, mix, numSamples, velocityGain)
            }
            InstrumentType.BASS, InstrumentType.BASS_5 -> {
                val baseNotes = floatArrayOf(98.00f, 73.42f, 55.00f, 41.20f, 30.87f)
                val base = baseNotes.getOrElse(note.stringIndex) { 41.20f }
                val freq = base * 2.0f.pow(note.fret / 12.0f)
                synthesizeBassTone(freq, mix, numSamples, velocityGain, note.effect)
            }
            InstrumentType.UKULELE -> {
                val baseNotes = floatArrayOf(440.00f, 329.63f, 261.63f, 392.00f)
                val base = baseNotes.getOrElse(note.stringIndex) { 261.63f }
                val freq = base * 2.0f.pow(note.fret / 12.0f)
                synthesizePluckTone(freq, mix, numSamples, velocityGain, note.effect, decaySpeed = 7.0f)
            }
            InstrumentType.KEYBOARD -> {
                val baseNotes = floatArrayOf(523.25f, 392.00f, 329.63f, 261.63f, 196.00f, 130.81f)
                val base = baseNotes.getOrElse(note.stringIndex) { 261.63f }
                val freq = base * 2.0f.pow(note.fret / 12.0f)
                synthesizePianoTone(freq, mix, numSamples, velocityGain)
            }
            else -> {
                // GUITAR, GUITAR_7, GUITAR_8
                val baseNotes = floatArrayOf(329.63f, 246.94f, 196.00f, 146.83f, 110.00f, 82.41f, 61.74f, 46.25f)
                val base = baseNotes.getOrElse(note.stringIndex) { 110.00f }
                val freq = base * 2.0f.pow(note.fret / 12.0f)
                synthesizePluckTone(freq, mix, numSamples, velocityGain, note.effect, decaySpeed = 4.2f)
            }
        }
    }

    private fun synthesizePluckTone(
        frequencyHz: Float,
        mix: FloatArray,
        numSamples: Int,
        gain: Float,
        effect: NoteEffect,
        decaySpeed: Float,
    ) {
        val twoPiF = 2.0 * PI * frequencyHz / sampleRate
        val isMuted = effect == NoteEffect.DEAD_NOTE
        val effectiveDecay = if (isMuted) 30.0f else decaySpeed
        val effectiveGain = if (isMuted) gain * 0.4f else gain

        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-effectiveDecay * t)
            val fundamental = sin(twoPiF * i)
            val h2 = sin(twoPiF * i * 2.0) * 0.35
            val h3 = sin(twoPiF * i * 3.0) * 0.15
            val h4 = sin(twoPiF * i * 4.0) * 0.05
            val sample = ((fundamental + h2 + h3 + h4) * decay * effectiveGain).toFloat()
            mix[i] += sample
        }
    }

    private fun synthesizeBassTone(
        frequencyHz: Float,
        mix: FloatArray,
        numSamples: Int,
        gain: Float,
        effect: NoteEffect,
    ) {
        val twoPiF = 2.0 * PI * frequencyHz / sampleRate
        val isMuted = effect == NoteEffect.DEAD_NOTE
        val effectiveDecay = if (isMuted) 20.0f else 2.8f
        val effectiveGain = if (isMuted) gain * 0.4f else gain * 1.1f

        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-effectiveDecay * t)
            val fundamental = sin(twoPiF * i)
            val h2 = sin(twoPiF * i * 2.0) * 0.2
            val sample = ((fundamental + h2) * decay * effectiveGain).toFloat()
            mix[i] += sample
        }
    }

    private fun synthesizePianoTone(
        frequencyHz: Float,
        mix: FloatArray,
        numSamples: Int,
        gain: Float,
    ) {
        val twoPiF = 2.0 * PI * frequencyHz / sampleRate
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-3.2 * t)
            val s = sin(twoPiF * i) + 0.4 * sin(twoPiF * i * 2.0) + 0.2 * sin(twoPiF * i * 3.0)
            mix[i] += (s * decay * gain * 0.8f).toFloat()
        }
    }

    private fun synthesizeDrumHit(
        drumIndex: Int,
        mix: FloatArray,
        numSamples: Int,
        gain: Float,
    ) {
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val sampleVal: Double = when (drumIndex) {
                0 -> { // Crash cymbal
                    val decay = exp(-6.0 * t)
                    val noise = (random.nextDouble() * 2.0 - 1.0) * 0.5
                    val tone = sin(2.0 * PI * 650.0 * t) * 0.3
                    (noise + tone) * decay
                }
                1 -> { // Hi-Hat
                    val decay = exp(-35.0 * t)
                    val noise = (random.nextDouble() * 2.0 - 1.0)
                    noise * decay * 0.6
                }
                2 -> { // Snare
                    val decay = exp(-14.0 * t)
                    val noise = (random.nextDouble() * 2.0 - 1.0) * 0.6
                    val tone = sin(2.0 * PI * 180.0 * t) * 0.4
                    (noise + tone) * decay
                }
                3 -> { // Tom
                    val decay = exp(-10.0 * t)
                    val freq = 130.0 * exp(-8.0 * t) + 60.0
                    sin(2.0 * PI * freq * t) * decay * 0.8
                }
                else -> { // Bass Drum (Kick)
                    val decay = exp(-12.0 * t)
                    val freq = 90.0 * exp(-15.0 * t) + 40.0
                    sin(2.0 * PI * freq * t) * decay * 1.1
                }
            }
            mix[i] += (sampleVal * gain).toFloat()
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

    fun seekTo(measureIndex: Int, beatIndex: Int = 0) {
        _currentMeasureIndex.value = measureIndex.coerceAtLeast(0)
        _currentBeatIndex.value = beatIndex.coerceAtLeast(0)
    }

    fun setSpeed(speed: Float) {
        _speedMultiplier.value = speed.coerceIn(0.25f, 2.5f)
    }

    fun setLoop(startMeasure: Int, endMeasure: Int) {
        loopStartMeasure = startMeasure
        loopEndMeasure = endMeasure
    }

    fun clearLoop() {
        loopStartMeasure = null
        loopEndMeasure = null
    }
}
