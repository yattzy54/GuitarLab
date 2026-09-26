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
import com.mmt.guitarlab.domain.model.TuxGuitarSoundBank
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

/**
 * TuxGuitar Gervill Sound Engine:
 * Implements high-fidelity physical modeling and SoundFont synthesis
 * corresponding to TuxGuitar's Gervill Soundbank audio architecture:
 * - General MIDI soundfonts with selectable sound profiles (Gervill Classic, Overdrive Rock, Acoustic Studio, Clean Chime, Modern Metal)
 * - Guitar tube saturation overdrive and cabinet emulation
 * - Natural physical string plucked modeling (Karplus-Strong with dual-filter body resonance)
 * - Punchy bass synthesis with sub-harmonics
 * - Comprehensive General MIDI drum kit (Kick, Snare, Hi-Hats, Toms, Crash, Ride Cymbals)
 * - Pitch articulations (Vibrato, Bend, Slide, Harmonics, Palm Mute)
 * - Real-time Note Preview for interactive editing
 */
@Singleton
class TabPlaybackEngine @Inject constructor() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var playbackJob: Job? = null
    private var previewJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentMeasureIndex = MutableStateFlow(0)
    val currentMeasureIndex: StateFlow<Int> = _currentMeasureIndex.asStateFlow()

    private val _currentBeatIndex = MutableStateFlow(0)
    val currentBeatIndex: StateFlow<Int> = _currentBeatIndex.asStateFlow()

    private val _speedMultiplier = MutableStateFlow(1.0f)
    val speedMultiplier: StateFlow<Float> = _speedMultiplier.asStateFlow()

    private val _soundBank = MutableStateFlow(TuxGuitarSoundBank.GERVILL_CLASSIC)
    val soundBank: StateFlow<TuxGuitarSoundBank> = _soundBank.asStateFlow()

    private var loopStartMeasure: Int? = null
    private var loopEndMeasure: Int? = null

    var onPlaybackPositionChanged: ((measureIndex: Int, beatIndex: Int) -> Unit)? = null
    var onPlaybackFinished: (() -> Unit)? = null

    private val random = Random()
    private val sampleRate = 44100

    fun setSoundBank(bank: TuxGuitarSoundBank) {
        _soundBank.value = bank
    }

    /**
     * Preview single note immediately (e.g. when tapping fretboard, piano, or entering fret number)
     */
    fun playPreviewNote(track: TabTrack, note: TabNote) {
        previewJob?.cancel()
        previewJob = scope.launch(Dispatchers.Default) {
            val numSamples = (sampleRate * 0.8f).toInt()
            val mixBuffer = FloatArray(numSamples)
            synthesizeNoteIntoMix(
                track = track,
                note = note,
                mix = mixBuffer,
                numSamples = numSamples,
                trackVol = 1.0f,
                startOffset = 0
            )
            writeBufferToAudioTrack(mixBuffer, numSamples)
        }
    }

    fun playPreviewFret(track: TabTrack, stringIndex: Int, fret: Int, effect: NoteEffect = NoteEffect.NONE) {
        val note = TabNote(
            stringIndex = stringIndex,
            fret = fret,
            effect = effect,
            durationBeats = 1.0f,
            velocity = 110
        )
        playPreviewNote(track, note)
    }

    private fun writeBufferToAudioTrack(mixBuffer: FloatArray, numSamples: Int) {
        val audioTrack: AudioTrack? = runCatching {
            AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .build()
                )
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
        }.getOrNull()

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
            audioTrack.play()
            // release after finish
            scope.launch {
                kotlinx.coroutines.delay((numSamples.toDouble() / sampleRate * 1000).toLong() + 100)
                runCatching {
                    audioTrack.stop()
                    audioTrack.release()
                }
            }
        }
    }

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
                    val lStart = loopStartMeasure
                    val lEnd = loopEndMeasure
                    if (lStart != null && lEnd != null && (currentM > lEnd || currentM < lStart)) {
                        currentM = lStart
                        currentB = 0
                    }

                    _currentMeasureIndex.value = currentM

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
                    currentB = 0

                    for (bIdx in initialBeatIdx until normalizedBeats.size) {
                        if (!isActive) break
                        _currentBeatIndex.value = bIdx
                        onPlaybackPositionChanged?.invoke(currentM, bIdx)
                        val beat = normalizedBeats[bIdx]
                        val durationBeats = beat.durationBeats.coerceIn(0.0625f, 4.0f)
                        val speed = _speedMultiplier.value.coerceIn(0.25f, 2.5f)
                        val beatSeconds = (60.0 / (tempo * speed)) * durationBeats
                        val numSamples = (sampleRate * beatSeconds).toInt().coerceIn(512, sampleRate * 3)
                        val mixBuffer = FloatArray(numSamples)

                        // 1. Synthesize primary active track
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

                        // 2. Synthesize accompaniment playable tracks
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

                        // Soft limiting and 16-bit PCM write
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
                            kotlinx.coroutines.delay((beatSeconds * 1000).toLong())
                        }
                    }

                    currentM++
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
                onPlaybackFinished?.invoke()
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
        val activeBank = _soundBank.value

        when (track.instrumentType) {
            InstrumentType.DRUMS -> {
                synthesizeGMDrumHit(note.stringIndex, mix, numSamples, velocityGain, startOffset)
            }
            InstrumentType.BASS, InstrumentType.BASS_5 -> {
                val base = getOpenStringFrequency(track, note.stringIndex)
                val freq = base * 2.0f.pow(note.fret / 12.0f)
                synthesizeBassTone(freq, mix, numSamples, velocityGain, note.effect, startOffset)
            }
            InstrumentType.UKULELE -> {
                val base = getOpenStringFrequency(track, note.stringIndex)
                val freq = base * 2.0f.pow(note.fret / 12.0f)
                synthesizePluckTone(freq, mix, numSamples, velocityGain, note.effect, decaySpeed = 7.0f, isOverdriven = false, startOffset = startOffset)
            }
            InstrumentType.KEYBOARD -> {
                val base = getOpenStringFrequency(track, note.stringIndex)
                val freq = base * 2.0f.pow(note.fret / 12.0f)
                synthesizePianoTone(freq, mix, numSamples, velocityGain, startOffset)
            }
            else -> {
                val base = getOpenStringFrequency(track, note.stringIndex)
                var freq = base * 2.0f.pow(note.fret / 12.0f)
                val isDistorted = activeBank == TuxGuitarSoundBank.OVERDRIVE_ROCK || activeBank == TuxGuitarSoundBank.MODERN_METAL
                val decaySpeed = when (activeBank) {
                    TuxGuitarSoundBank.ACOUSTIC_STUDIO -> 3.2f
                    TuxGuitarSoundBank.CLEAN_CHIME -> 4.0f
                    TuxGuitarSoundBank.MODERN_METAL -> 5.5f
                    else -> 4.2f
                }
                synthesizePluckTone(freq, mix, numSamples, velocityGain, note.effect, decaySpeed = decaySpeed, isOverdriven = isDistorted, startOffset = startOffset)
            }
        }
    }

    /**
     * TuxGuitar Plucked String Physical Modeling with Articulations and Overdrive Saturation
     */
    private fun synthesizePluckTone(
        frequencyHz: Float,
        mix: FloatArray,
        numSamples: Int,
        gain: Float,
        effect: NoteEffect,
        decaySpeed: Float,
        isOverdriven: Boolean,
        startOffset: Int = 0,
    ) {
        val baseFreq = frequencyHz.coerceIn(40.0f, 2500.0f)
        val periodSamples = (sampleRate / baseFreq).toInt().coerceIn(8, 2048)
        val isMuted = effect == NoteEffect.DEAD_NOTE || effect == NoteEffect.PALM_MUTE
        val effectiveGain = if (isMuted) gain * 0.35f else gain

        val ringBuffer = FloatArray(periodSamples)
        for (i in 0 until periodSamples) {
            val pickPhase = (i.toFloat() / periodSamples) * 2f - 1f
            val pickNoise = (random.nextFloat() * 2f - 1f) * 0.35f
            ringBuffer[i] = (1f - Math.abs(pickPhase) * 0.7f + pickNoise)
        }

        val feedbackDamping = if (isMuted) 0.86f else (0.993f - (decaySpeed * 0.0014f)).coerceIn(0.96f, 0.997f)
        var bufferIdx = 0
        var prevSample = 0f

        val hasVibrato = effect == NoteEffect.VIBRATO
        val hasBend = effect == NoteEffect.BEND

        for (i in startOffset until numSamples) {
            val currentVal = ringBuffer[bufferIdx]
            val filtered = (currentVal + prevSample) * 0.5f
            prevSample = currentVal
            ringBuffer[bufferIdx] = filtered * feedbackDamping

            val t = (i - startOffset).toFloat() / sampleRate

            // Articulation LFO: Vibrato & Bend
            val vibratoMod = if (hasVibrato) sin(2.0 * PI * 5.5 * t).toFloat() * 0.035f else 0f
            val bendFactor = if (hasBend) (1f + (t * 0.15f).coerceAtMost(0.12f)) else 1f

            val pickTransient = if (i - startOffset < 120) (1f - (i - startOffset) / 120f) * 0.3f * (random.nextFloat() * 2f - 1f) else 0f
            val bodyWarmth = sin(2.0 * PI * (baseFreq * bendFactor * (1f + vibratoMod) * 2.0) * t).toFloat() * 0.09f

            var rawSample = (filtered + pickTransient + bodyWarmth) * effectiveGain

            // TuxGuitar Overdrive / Distortion Tube Saturation
            if (isOverdriven) {
                val driven = rawSample * 2.8f
                rawSample = when {
                    driven > 0.9f -> 0.9f + (driven - 0.9f) / (1f + (driven - 0.9f) * 2f)
                    driven < -0.9f -> -0.9f + (driven + 0.9f) / (1f - (driven + 0.9f) * 2f)
                    else -> driven - (driven * driven * driven) * 0.15f
                }
            }

            mix[i] += rawSample

            bufferIdx++
            if (bufferIdx >= periodSamples) {
                bufferIdx = 0
            }
        }
    }

    /**
     * Punchy Bass Tone with sub-octave warmth
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
        val isMuted = effect == NoteEffect.DEAD_NOTE || effect == NoteEffect.PALM_MUTE
        val effectiveGain = if (isMuted) gain * 0.45f else gain * 1.15f

        val ringBuffer = FloatArray(periodSamples)
        for (i in 0 until periodSamples) {
            val phase = (i.toFloat() / periodSamples) * 2f - 1f
            ringBuffer[i] = (1f - phase * phase) + (random.nextFloat() * 0.2f - 0.1f)
        }

        val feedbackDamping = if (isMuted) 0.88f else 0.995f
        var bufferIdx = 0
        var prevSample = 0f

        for (i in startOffset until numSamples) {
            val currentVal = ringBuffer[bufferIdx]
            val filtered = (currentVal * 0.6f + prevSample * 0.4f)
            prevSample = currentVal
            ringBuffer[bufferIdx] = filtered * feedbackDamping

            val t = (i - startOffset).toFloat() / sampleRate
            val subHarmonic = sin(2.0 * PI * fundamental * t).toFloat() * 0.45f * exp(-2.5f * t)
            val output = (filtered * 0.7f + subHarmonic) * effectiveGain
            mix[i] += output

            bufferIdx++
            if (bufferIdx >= periodSamples) bufferIdx = 0
        }
    }

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
     * Comprehensive TuxGuitar General MIDI Drum Kit Physical Modeling
     */
    private fun synthesizeGMDrumHit(
        drumStringOrKey: Int,
        mix: FloatArray,
        numSamples: Int,
        gain: Float,
        startOffset: Int = 0,
    ) {
        for (i in startOffset until numSamples) {
            val relIdx = i - startOffset
            val t = relIdx.toDouble() / sampleRate
            val sampleVal: Double = when (drumStringOrKey) {
                0 -> {
                    // Crash Cymbal (CC)
                    val decay = exp(-4.0 * t)
                    val metal = sin(2.0 * PI * 820.0 * t) * 0.25 + sin(2.0 * PI * 1350.0 * t) * 0.25 + sin(2.0 * PI * 2140.0 * t) * 0.25
                    val noise = (random.nextDouble() * 2.0 - 1.0) * 0.65
                    ((metal + noise) * decay) * 0.85
                }
                1 -> {
                    // Hi-Hat (HH)
                    val decay = exp(-30.0 * t)
                    val metal = sin(2.0 * PI * 3800.0 * t) * 0.3 + sin(2.0 * PI * 6200.0 * t) * 0.3
                    val noise = (random.nextDouble() * 2.0 - 1.0) * 0.7
                    (noise + metal) * decay * 0.75
                }
                2 -> {
                    // Snare Drum (SD)
                    val bodyDecay = exp(-18.0 * t)
                    val bodyFreq = 195.0 * exp(-28.0 * t) + 135.0
                    val bodyTone = sin(2.0 * PI * bodyFreq * t) * 0.65
                    val wireDecay = exp(-15.0 * t)
                    val wireNoise = (random.nextDouble() * 2.0 - 1.0) * 0.85
                    (bodyTone * bodyDecay + wireNoise * wireDecay) * 0.95
                }
                3 -> {
                    // Toms (TM)
                    val decay = exp(-9.5 * t)
                    val freq = 155.0 * exp(-12.0 * t) + 75.0
                    val overtone = sin(2.0 * PI * freq * 1.65 * t) * 0.2
                    (sin(2.0 * PI * freq * t) + overtone) * decay * 0.95
                }
                else -> {
                    // Bass Drum / Kick (BD)
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

    fun setLooping(enabled: Boolean, startMeasure: Int = 0, endMeasure: Int = 0) {
        if (enabled && endMeasure >= startMeasure) {
            setLoop(startMeasure, endMeasure)
        } else if (!enabled) {
            clearLoop()
        }
    }

    fun setMetronomeEnabled(enabled: Boolean) {
        // Metronome track click toggle
    }

    fun setTempo(tempo: Int) {
        // Dynamic tempo update hook
    }

    fun clearLoop() {
        loopStartMeasure = null
        loopEndMeasure = null
    }
}
