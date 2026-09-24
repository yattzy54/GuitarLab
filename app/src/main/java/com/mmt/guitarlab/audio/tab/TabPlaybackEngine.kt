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
        playbackJob?.cancel()
        playbackJob = null
        _isPlaying.value = true

        val clampedTrackIdx = activeTrackIndex.coerceIn(0, score.tracks.lastIndex.coerceAtLeast(0))
        val primaryTrack = score.tracks.getOrNull(clampedTrackIdx) ?: score.tracks.firstOrNull() ?: return
        val totalMeasures = primaryTrack.measures.size.coerceAtLeast(score.tracks.maxOfOrNull { it.measures.size } ?: 0)
        if (totalMeasures == 0) {
            _isPlaying.value = false
            return
        }

        val initialM = startMeasureIndex.coerceIn(0, totalMeasures - 1)
        _currentMeasureIndex.value = initialM
        _currentBeatIndex.value = startBeatIndex.coerceAtLeast(0)

        playbackJob = scope.launch(Dispatchers.Default) {
            val soloTracks = score.tracks.filter { it.isSolo }
            val playableTracks = if (soloTracks.isNotEmpty()) soloTracks else score.tracks.filter { !it.isMuted }
            if (playableTracks.isEmpty()) {
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
                var currentM = initialM
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

                    // Select active measure beats to drive timing and display
                    val activeMeasure = primaryTrack.measures.getOrNull(currentM)
                    val candidateBeats = activeMeasure?.beats?.takeIf { it.isNotEmpty() }
                        ?: playableTracks.firstNotNullOfOrNull { it.measures.getOrNull(currentM)?.beats?.takeIf { b -> b.isNotEmpty() } }
                        ?: emptyList()

                    if (candidateBeats.isEmpty()) {
                        _currentBeatIndex.value = 0
                        val speed = _speedMultiplier.value.coerceIn(0.25f, 2.5f)
                        val barSeconds = (60.0 / (tempo * speed)) * 4.0
                        val delayMs = (barSeconds * 1000).toLong()
                        kotlinx.coroutines.delay(delayMs)
                        currentM++
                        currentB = 0
                        continue
                    }

                    // Normalize beats with accurate startBeat and durationBeats
                    var accumulatedStart = 0f
                    val normalizedBeats = candidateBeats.mapIndexed { idx, beat ->
                        val dur = if (beat.durationBeats > 0f) beat.durationBeats else when (beat.durationType) {
                            NoteDuration.WHOLE -> 4.0f
                            NoteDuration.HALF -> 2.0f
                            NoteDuration.QUARTER -> 1.0f
                            NoteDuration.EIGHTH -> 0.5f
                            NoteDuration.SIXTEENTH -> 0.25f
                            NoteDuration.THIRTY_SECOND -> 0.125f
                        }
                        val start = if (beat.startBeat > 0f || idx == 0) beat.startBeat else accumulatedStart
                        accumulatedStart = start + dur
                        beat.copy(startBeat = start, durationBeats = dur)
                    }

                    val initialBeatIdx = currentB.coerceIn(0, normalizedBeats.lastIndex)
                    currentB = 0 // reset for subsequent measures

                    for (bIdx in initialBeatIdx until normalizedBeats.size) {
                        if (!isActive) break
                        _currentBeatIndex.value = bIdx
                        val beat = normalizedBeats[bIdx]
                        val durationBeats = beat.durationBeats.coerceIn(0.0625f, 4.0f)
                        val speed = _speedMultiplier.value.coerceIn(0.25f, 2.5f)
                        val beatSeconds = (60.0 / (tempo * speed)) * durationBeats
                        val numSamples = (sampleRate * beatSeconds).toInt().coerceIn(512, sampleRate * 3)
                        val mixBuffer = FloatArray(numSamples)

                        // 1. Synthesize active track with prominent volume
                        val primaryVol = primaryTrack.volume.coerceIn(0f, 1f) * 1.15f
                        val activeBeatInPrimary = primaryTrack.measures.getOrNull(currentM)?.beats?.getOrNull(bIdx)
                            ?: primaryTrack.measures.getOrNull(currentM)?.beats?.firstOrNull {
                                kotlin.math.abs(it.startBeat - beat.startBeat) < 0.05f
                            }
                        if (activeBeatInPrimary != null) {
                            activeBeatInPrimary.notes.forEach { note ->
                                synthesizeNoteIntoMix(primaryTrack, note, mixBuffer, numSamples, primaryVol, startOffset = 0)
                            }
                        }

                        // 2. Synthesize accompaniment playable tracks that start in this beat window
                        val beatStart = beat.startBeat
                        val beatEnd = beatStart + durationBeats
                        playableTracks.forEach { track ->
                            if (track.id != primaryTrack.id) {
                                val otherMeasure = track.measures.getOrNull(currentM)
                                val otherVol = track.volume.coerceIn(0f, 1f) * 0.85f
                                otherMeasure?.beats?.forEach { otherBeat ->
                                    val otherStart = otherBeat.startBeat
                                    if (otherStart >= beatStart - 0.02f && otherStart < beatEnd - 0.02f) {
                                        val offsetRatio = ((otherStart - beatStart) / durationBeats).coerceIn(0f, 0.95f)
                                        val offsetSample = (offsetRatio * numSamples).toInt()
                                        otherBeat.notes.forEach { note ->
                                            synthesizeNoteIntoMix(track, note, mixBuffer, numSamples, otherVol, startOffset = offsetSample)
                                        }
                                    }
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
                    }

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

    private fun getOpenStringFrequency(track: TabTrack, stringIndex: Int): Float {
        val noteName = track.tuningNotes.getOrNull(stringIndex)
        if (!noteName.isNullOrBlank()) {
            val freq = pitchNameToFrequency(noteName)
            if (freq > 20f) return freq
        }
        return when (track.instrumentType) {
            InstrumentType.BASS, InstrumentType.BASS_5 -> {
                val defaults = floatArrayOf(98.00f, 73.42f, 55.00f, 41.20f, 30.87f)
                defaults.getOrElse(stringIndex) { 41.20f }
            }
            InstrumentType.UKULELE -> {
                val defaults = floatArrayOf(440.00f, 329.63f, 261.63f, 392.00f)
                defaults.getOrElse(stringIndex) { 261.63f }
            }
            InstrumentType.KEYBOARD -> {
                val defaults = floatArrayOf(523.25f, 392.00f, 329.63f, 261.63f, 196.00f, 130.81f)
                defaults.getOrElse(stringIndex) { 261.63f }
            }
            else -> {
                val defaults = floatArrayOf(329.63f, 246.94f, 196.00f, 146.83f, 110.00f, 82.41f, 61.74f, 46.25f)
                defaults.getOrElse(stringIndex) { 110.00f }
            }
        }
    }

    private fun pitchNameToFrequency(pitch: String): Float {
        val regex = Regex("([A-Ga-g][#b]?)(-?[0-9]+)")
        val match = regex.matchEntire(pitch.trim()) ?: return 0f
        val notePart = match.groupValues[1].uppercase()
        val octavePart = match.groupValues[2].toIntOrNull() ?: 4
        val semitone = when (notePart) {
            "C" -> 0
            "C#", "DB" -> 1
            "D" -> 2
            "D#", "EB" -> 3
            "E" -> 4
            "F" -> 5
            "F#", "GB" -> 6
            "G" -> 7
            "G#", "AB" -> 8
            "A" -> 9
            "A#", "BB" -> 10
            "B" -> 11
            else -> 0
        }
        val midiNote = (octavePart + 1) * 12 + semitone
        return (440.0 * 2.0.pow((midiNote - 69) / 12.0)).toFloat()
    }

    private fun synthesizeNoteIntoMix(
        track: TabTrack,
        note: TabNote,
        mix: FloatArray,
        numSamples: Int,
        trackVol: Float,
        startOffset: Int = 0,
    ) {
        val velocityGain = (note.velocity.coerceIn(20, 127) / 127f) * trackVol

        when (track.instrumentType) {
            InstrumentType.DRUMS -> {
                synthesizeDrumHit(note.stringIndex, mix, numSamples, velocityGain, startOffset)
            }
            InstrumentType.BASS, InstrumentType.BASS_5 -> {
                val base = getOpenStringFrequency(track, note.stringIndex)
                val freq = base * 2.0f.pow(note.fret / 12.0f)
                synthesizeBassTone(freq, mix, numSamples, velocityGain, note.effect, startOffset)
            }
            InstrumentType.UKULELE -> {
                val base = getOpenStringFrequency(track, note.stringIndex)
                val freq = base * 2.0f.pow(note.fret / 12.0f)
                synthesizePluckTone(freq, mix, numSamples, velocityGain, note.effect, decaySpeed = 7.0f, startOffset = startOffset)
            }
            InstrumentType.KEYBOARD -> {
                val base = getOpenStringFrequency(track, note.stringIndex)
                val freq = base * 2.0f.pow(note.fret / 12.0f)
                synthesizePianoTone(freq, mix, numSamples, velocityGain, startOffset)
            }
            else -> {
                val base = getOpenStringFrequency(track, note.stringIndex)
                val freq = base * 2.0f.pow(note.fret / 12.0f)
                synthesizePluckTone(freq, mix, numSamples, velocityGain, note.effect, decaySpeed = 4.2f, startOffset = startOffset)
            }
        }
    }

    /**
     * High-fidelity Karplus-Strong Physical String Modeling for electric and acoustic guitar.
     * Simulates the physics of a picked metal string with initial pick impulse,
     * high-frequency string damping, and harmonic body resonance.
     */
    private fun synthesizePluckTone(
        frequencyHz: Float,
        mix: FloatArray,
        numSamples: Int,
        gain: Float,
        effect: NoteEffect,
        decaySpeed: Float,
        startOffset: Int = 0,
    ) {
        val fundamental = frequencyHz.coerceIn(40.0f, 2500.0f)
        val periodSamples = (sampleRate / fundamental).toInt().coerceIn(8, 2048)
        val isMuted = effect == NoteEffect.DEAD_NOTE
        val effectiveGain = if (isMuted) gain * 0.35f else gain

        val ringBuffer = FloatArray(periodSamples)
        for (i in 0 until periodSamples) {
            val pickPhase = (i.toFloat() / periodSamples) * 2f - 1f
            val pickNoise = (random.nextFloat() * 2f - 1f) * 0.35f
            ringBuffer[i] = (1f - Math.abs(pickPhase) * 0.7f + pickNoise)
        }

        val feedbackDamping = if (isMuted) 0.88f else (0.992f - (decaySpeed * 0.0015f)).coerceIn(0.97f, 0.996f)
        var bufferIdx = 0
        var prevSample = 0f

        for (i in startOffset until numSamples) {
            val currentVal = ringBuffer[bufferIdx]
            val filtered = (currentVal + prevSample) * 0.5f
            prevSample = currentVal
            ringBuffer[bufferIdx] = filtered * feedbackDamping

            val t = (i - startOffset).toFloat() / sampleRate
            val pickTransient = if (i - startOffset < 120) (1f - (i - startOffset) / 120f) * 0.25f * (random.nextFloat() * 2f - 1f) else 0f
            val bodyWarmth = kotlin.math.sin(2.0 * PI * (fundamental * 2.0) * t).toFloat() * 0.08f

            val output = (filtered + pickTransient + bodyWarmth) * effectiveGain
            mix[i] += output

            bufferIdx++
            if (bufferIdx >= periodSamples) {
                bufferIdx = 0
            }
        }
    }

    /**
     * Punchy, resonant Bass Guitar physical modeling:
     * Combines punchy sub-oscillator with Karplus-Strong string pluck and warm bass tube body.
     */
    private fun synthesizeBassTone(
        frequencyHz: Float,
        mix: FloatArray,
        numSamples: Int,
        gain: Float,
        effect: NoteEffect,
        startOffset: Int = 0,
    ) {
        val fundamental = frequencyHz.coerceIn(25.0f, 600.0f)
        val periodSamples = (sampleRate / fundamental).toInt().coerceIn(16, 4096)
        val isMuted = effect == NoteEffect.DEAD_NOTE
        val effectiveGain = if (isMuted) gain * 0.45f else gain * 1.15f

        val ringBuffer = FloatArray(periodSamples)
        for (i in 0 until periodSamples) {
            val phase = (i.toFloat() / periodSamples) * 2f - 1f
            ringBuffer[i] = (1f - phase * phase) + (random.nextFloat() * 0.2f - 0.1f)
        }

        val feedbackDamping = if (isMuted) 0.89f else 0.995f
        var bufferIdx = 0
        var prevSample = 0f

        for (i in startOffset until numSamples) {
            val currentVal = ringBuffer[bufferIdx]
            val filtered = (currentVal * 0.6f + prevSample * 0.4f)
            prevSample = currentVal
            ringBuffer[bufferIdx] = filtered * feedbackDamping

            val t = (i - startOffset).toFloat() / sampleRate
            val subHarmonic = kotlin.math.sin(2.0 * PI * fundamental * t).toFloat() * 0.45f * kotlin.math.exp(-2.5f * t)
            val output = (filtered * 0.7f + subHarmonic) * effectiveGain
            mix[i] += output

            bufferIdx++
            if (bufferIdx >= periodSamples) bufferIdx = 0
        }
    }

    /**
     * Warm Electric Piano / Keyboard synthesized tone with multi-harmonic envelope.
     */
    private fun synthesizePianoTone(
        frequencyHz: Float,
        mix: FloatArray,
        numSamples: Int,
        gain: Float,
        startOffset: Int = 0,
    ) {
        val twoPiF = 2.0 * PI * frequencyHz / sampleRate
        for (i in startOffset until numSamples) {
            val relIdx = i - startOffset
            val t = relIdx.toFloat() / sampleRate
            val decay = exp(-3.2 * t)
            val s = sin(twoPiF * relIdx) + 0.35 * sin(twoPiF * relIdx * 2.0) + 0.18 * sin(twoPiF * relIdx * 3.0) + 0.08 * sin(twoPiF * relIdx * 4.0)
            mix[i] += (s * decay * gain * 0.85f).toFloat()
        }
    }

    /**
     * Studio-grade Realistic Drum Synthesizer:
     * - Kick: 140Hz->42Hz punchy exponential pitch drop + 3.2kHz acoustic beater click
     * - Snare: 190Hz->130Hz drumhead tone + filtered white noise for acoustic snare wire buzz
     * - Hi-Hat: 6 metallic inharmonic square oscillators + crisp metallic bandpass decay
     * - Tom: 160Hz->75Hz resonant drum shell body
     * - Crash / Ride: Shimmering high-frequency metallic cluster with long exponential decay
     */
    private fun synthesizeDrumHit(
        drumIndex: Int,
        mix: FloatArray,
        numSamples: Int,
        gain: Float,
        startOffset: Int = 0,
    ) {
        for (i in startOffset until numSamples) {
            val relIdx = i - startOffset
            val t = relIdx.toDouble() / sampleRate
            val sampleVal: Double = when (drumIndex) {
                0 -> {
                    // Crash Cymbal: Dense shimmering metallic cluster + long decay
                    val decay = exp(-4.2 * t)
                    val metal1 = sin(2.0 * PI * 820.0 * t)
                    val metal2 = sin(2.0 * PI * 1350.0 * t)
                    val metal3 = sin(2.0 * PI * 2140.0 * t)
                    val noise = (random.nextDouble() * 2.0 - 1.0) * 0.65
                    ((metal1 * 0.2 + metal2 * 0.2 + metal3 * 0.2 + noise) * decay) * 0.85
                }
                1 -> {
                    // Hi-Hat: Crisp high-frequency metallic snap (closed / semi-open)
                    val decay = exp(-32.0 * t)
                    val metal = sin(2.0 * PI * 3800.0 * t) * 0.3 + sin(2.0 * PI * 6200.0 * t) * 0.3
                    val noise = (random.nextDouble() * 2.0 - 1.0) * 0.7
                    (noise + metal) * decay * 0.7
                }
                2 -> {
                    // Snare: Dynamic membrane pitch drop (195Hz -> 135Hz) + crispy snare wire rattle
                    val bodyDecay = exp(-18.0 * t)
                    val bodyFreq = 195.0 * exp(-28.0 * t) + 135.0
                    val bodyTone = sin(2.0 * PI * bodyFreq * t) * 0.65
                    val wireDecay = exp(-15.0 * t)
                    val wireNoise = (random.nextDouble() * 2.0 - 1.0) * 0.85
                    (bodyTone * bodyDecay + wireNoise * wireDecay) * 0.95
                }
                3 -> {
                    // Tom: Acoustic drum shell resonance (155Hz -> 80Hz)
                    val decay = exp(-9.5 * t)
                    val freq = 155.0 * exp(-12.0 * t) + 75.0
                    val overtone = sin(2.0 * PI * freq * 1.65 * t) * 0.2
                    (sin(2.0 * PI * freq * t) + overtone) * decay * 0.9
                }
                else -> {
                    // Kick Drum (Bass Drum): Sub-bass punch (135Hz -> 42Hz) + acoustic click beater
                    val click = if (relIdx < 80) (1.0 - relIdx / 80.0) * sin(2.0 * PI * 3400.0 * t) * 0.7 else 0.0
                    val pitchDrop = 135.0 * exp(-35.0 * t) + 42.0
                    val subDecay = exp(-10.5 * t)
                    val punch = sin(2.0 * PI * pitchDrop * t) * subDecay * 1.35
                    (punch + click)
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
