package com.mmt.guitarlab.data.remote

import android.util.Log
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.NoteDuration
import com.mmt.guitarlab.domain.model.NoteEffect
import com.mmt.guitarlab.domain.model.TabBeat
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.domain.model.TabNote
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.UUID
import java.util.zip.GZIPInputStream
import javax.inject.Inject
import javax.inject.Singleton

data class SongsterrTrackSummary(
    val index: Int,
    val name: String,
    val instrument: String,
    val views: Int,
    val tuning: List<Int>,
)

data class SongsterrSearchResult(
    val songId: Int,
    val title: String,
    val artist: String,
    val hasChords: Boolean,
    val tracks: List<SongsterrTrackSummary>,
)

@Singleton
class SongsterrApiService @Inject constructor() {

    private val primaryCdn = "https://dqsljvtekg760.cloudfront.net"
    private val fallbackCdn = "https://d3d3l6a6rcgkaf.cloudfront.net"

    suspend fun searchSongs(query: String): Result<List<SongsterrSearchResult>> = withContext(Dispatchers.IO) {
        runCatching {
            val encoded = URLEncoder.encode(query.trim(), "UTF-8")
            val urlString = "https://www.songsterr.com/api/songs?pattern=$encoded"
            val jsonString = fetchStringFromUrl(urlString)
            val jsonArray = JSONArray(jsonString)
            val results = mutableListOf<SongsterrSearchResult>()

            for (i in 0 until jsonArray.length().coerceAtMost(30)) {
                val obj = jsonArray.getJSONObject(i)
                val songId = obj.optInt("songId", 0)
                if (songId <= 0) continue

                val title = obj.optString("title", "Unknown Title")
                val artist = obj.optString("artist", "Unknown Artist")
                val hasChords = obj.optBoolean("hasChords", false)

                val tracksArray = obj.optJSONArray("tracks")
                val tracksList = mutableListOf<SongsterrTrackSummary>()
                if (tracksArray != null) {
                    for (t in 0 until tracksArray.length()) {
                        val trkObj = tracksArray.getJSONObject(t)
                        val trkName = trkObj.optString("name", "Track ${t + 1}")
                        val trkInstrument = trkObj.optString("instrument", "Guitar")
                        val views = trkObj.optInt("views", 0)

                        val tuningArray = trkObj.optJSONArray("tuning")
                        val tuning = mutableListOf<Int>()
                        if (tuningArray != null) {
                            for (tn in 0 until tuningArray.length()) {
                                tuning.add(tuningArray.getInt(tn))
                            }
                        }

                        tracksList.add(
                            SongsterrTrackSummary(
                                index = t,
                                name = trkName,
                                instrument = trkInstrument,
                                views = views,
                                tuning = tuning
                            )
                        )
                    }
                }

                results.add(
                    SongsterrSearchResult(
                        songId = songId,
                        title = title,
                        artist = artist,
                        hasChords = hasChords,
                        tracks = tracksList
                    )
                )
            }
            results
        }
    }

    suspend fun fetchSongScore(songId: Int, requestedTrackIndex: Int? = null): Result<TabScore> = withContext(Dispatchers.IO) {
        runCatching {
            // 1. Fetch metadata
            val metaUrl = "https://www.songsterr.com/api/meta/$songId"
            val metaString = fetchStringFromUrl(metaUrl)
            val metaJson = JSONObject(metaString)

            val title = metaJson.optString("title", "Song")
            val artist = metaJson.optString("artist", "Artist")
            val revisionId = metaJson.getInt("revisionId")
            val image = metaJson.getString("image")

            val tracksArray = metaJson.getJSONArray("tracks")
            if (tracksArray.length() == 0) {
                error("В табулатуре нет дорожек")
            }

            // Find best track index if not specified (prefer distortion/electric guitar or bass)
            val targetTrackIndex = requestedTrackIndex ?: run {
                var bestIdx = 0
                var bestViews = -1
                for (t in 0 until tracksArray.length()) {
                    val trk = tracksArray.getJSONObject(t)
                    val isVocal = trk.optBoolean("isVocalTrack", false)
                    val isEmpty = trk.optBoolean("isEmpty", false)
                    if (isVocal || isEmpty) continue

                    val views = trk.optInt("views", 0)
                    val instrument = trk.optString("instrument", "")
                    val isGuitar = instrument.contains("Guitar", ignoreCase = true) || instrument.contains("Bass", ignoreCase = true)
                    val scoreWeight = views + (if (isGuitar) 50000 else 0)

                    if (scoreWeight > bestViews) {
                        bestViews = scoreWeight
                        bestIdx = t
                    }
                }
                bestIdx
            }

            // 2. Fetch track JSON from CDN
            val trackJsonString = fetchTrackJsonFromCdn(songId, revisionId, image, targetTrackIndex)
            val trackJson = JSONObject(trackJsonString)

            // 3. Parse track JSON into TabScore
            parseSongsterrTrackToScore(
                songId = songId,
                title = title,
                artist = artist,
                metaTracks = tracksArray,
                activeTrackIdx = targetTrackIndex,
                trackJson = trackJson
            )
        }
    }

    private fun fetchTrackJsonFromCdn(songId: Int, revisionId: Int, image: String, trackIndex: Int): String {
        return try {
            val primaryUrl = "$primaryCdn/$songId/$revisionId/$image/$trackIndex.json"
            fetchStringFromUrl(primaryUrl)
        } catch (e: Exception) {
            Log.w("SongsterrApiService", "Primary CDN failed, falling back: ${e.message}")
            val fallbackUrl = "$fallbackCdn/$songId/$revisionId/$image/$trackIndex.json"
            fetchStringFromUrl(fallbackUrl)
        }
    }

    private fun fetchStringFromUrl(urlString: String): String {
        val url = URL(urlString)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 12000
            readTimeout = 12000
            setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            setRequestProperty("Accept", "application/json, text/plain, */*")
            setRequestProperty("Accept-Encoding", "gzip, deflate")
        }

        val code = conn.responseCode
        if (code !in 200..299) {
            throw java.io.IOException("HTTP $code for $urlString")
        }

        val rawBytes = conn.inputStream.use { it.readBytes() }
        val isGzipped = (rawBytes.size >= 2 && rawBytes[0] == 0x1f.toByte() && (rawBytes[1].toInt() and 0xFF) == 0x8b)
        return if (isGzipped) {
            java.util.zip.GZIPInputStream(java.io.ByteArrayInputStream(rawBytes))
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
        } else {
            String(rawBytes, Charsets.UTF_8)
        }
    }

    private fun parseSongsterrTrackToScore(
        songId: Int,
        title: String,
        artist: String,
        metaTracks: JSONArray,
        activeTrackIdx: Int,
        trackJson: JSONObject
    ): TabScore {
        val trackName = trackJson.optString("name", "Guitar")
        val instrumentName = trackJson.optString("instrument", "Guitar")

        // Parse tuning
        val tuningArray = trackJson.optJSONArray("tuning")
        val tuningMidi = mutableListOf<Int>()
        if (tuningArray != null) {
            for (i in 0 until tuningArray.length()) {
                tuningMidi.add(tuningArray.getInt(i))
            }
        } else {
            tuningMidi.addAll(listOf(64, 59, 55, 50, 45, 40))
        }

        val stringCount = tuningMidi.size.coerceIn(4, 8)
        val tuningNotes = tuningMidi.map { midiToPitchName(it) }
        val stringLabels = tuningNotes.map { it.replace(Regex("[0-9]"), "") }
        val tuningDisplayName = detectTuningName(tuningMidi)

        // Parse tempo from automations
        var tempoBpm = 120
        val automations = trackJson.optJSONObject("automations")
        if (automations != null) {
            val tempoList = automations.optJSONArray("tempo")
            if (tempoList != null && tempoList.length() > 0) {
                tempoBpm = tempoList.getJSONObject(0).optInt("bpm", 120)
            }
        }

        // Determine instrument type
        val instrumentType = when {
            instrumentName.contains("Bass", ignoreCase = true) -> if (stringCount >= 5) InstrumentType.BASS_5 else InstrumentType.BASS
            stringCount == 7 -> InstrumentType.GUITAR_7
            stringCount == 8 -> InstrumentType.GUITAR_8
            instrumentName.contains("Drum", ignoreCase = true) -> InstrumentType.DRUMS
            else -> InstrumentType.GUITAR
        }

        // Parse measures
        val measuresArray = trackJson.optJSONArray("measures") ?: JSONArray()
        val parsedMeasures = mutableListOf<TabMeasure>()

        var currentNumerator = 4
        var currentDenominator = 4

        for (m in 0 until measuresArray.length()) {
            val mObj = measuresArray.getJSONObject(m)
            val barNumber = m + 1

            // Signature
            val sigArray = mObj.optJSONArray("signature")
            if (sigArray != null && sigArray.length() >= 2) {
                currentNumerator = sigArray.getInt(0)
                currentDenominator = sigArray.getInt(1)
            }

            // Voices
            val voicesArray = mObj.optJSONArray("voices")
            val beatsList = mutableListOf<TabBeat>()
            var measureHasPalmMute = false

            if (voicesArray != null && voicesArray.length() > 0) {
                val v0 = voicesArray.getJSONObject(0)
                val beatsArray = v0.optJSONArray("beats")
                if (beatsArray != null) {
                    var beatStart = 0f

                    for (b in 0 until beatsArray.length()) {
                        val bObj = beatsArray.getJSONObject(b)
                        val durationArray = bObj.optJSONArray("duration")
                        val durNum = durationArray?.optInt(0, 1) ?: 1
                        val durDen = durationArray?.optInt(1, 4) ?: 4

                        val durBeats = (durNum.toFloat() / durDen.toFloat()) * 4f
                        val durationType = when (durDen) {
                            1 -> NoteDuration.WHOLE
                            2 -> NoteDuration.HALF
                            4 -> NoteDuration.QUARTER
                            8 -> NoteDuration.EIGHTH
                            16 -> NoteDuration.SIXTEENTH
                            else -> NoteDuration.EIGHTH
                        }

                        // Notes in beat
                        val notesArray = bObj.optJSONArray("notes")
                        val notesList = mutableListOf<TabNote>()

                        if (notesArray != null) {
                            for (n in 0 until notesArray.length()) {
                                val nObj = notesArray.getJSONObject(n)
                                val isRest = nObj.optBoolean("rest", false)
                                if (isRest) continue

                                val fret = nObj.optInt("fret", 0)
                                val stringIdx = nObj.optInt("string", 0).coerceIn(0, stringCount - 1)

                                val isPalmMute = nObj.optBoolean("palmMute", false) || bObj.optBoolean("palmMute", false)
                                if (isPalmMute) measureHasPalmMute = true

                                val effect = when {
                                    isPalmMute -> NoteEffect.PALM_MUTE
                                    nObj.optBoolean("dead", false) -> NoteEffect.DEAD_NOTE
                                    nObj.has("bend") -> NoteEffect.BEND
                                    nObj.has("slide") -> NoteEffect.SLIDE
                                    nObj.optBoolean("vibrato", false) -> NoteEffect.VIBRATO
                                    nObj.optBoolean("hammer", false) -> NoteEffect.HAMMER_ON
                                    else -> NoteEffect.NONE
                                }

                                notesList.add(
                                    TabNote(
                                        stringIndex = stringIdx,
                                        fret = fret,
                                        effect = effect,
                                        startBeat = beatStart,
                                        durationBeats = durBeats
                                    )
                                )
                            }
                        }

                        beatsList.add(
                            TabBeat(
                                notes = notesList,
                                startBeat = beatStart,
                                durationBeats = durBeats,
                                durationType = durationType
                            )
                        )
                        beatStart += durBeats
                    }
                }
            }

            parsedMeasures.add(
                TabMeasure(
                    number = barNumber,
                    timeSignatureNumerator = currentNumerator,
                    timeSignatureDenominator = currentDenominator,
                    tempoBpm = tempoBpm,
                    palmMute = measureHasPalmMute,
                    palmMuteLabel = if (measureHasPalmMute) "P.M." else null,
                    beats = beatsList
                )
            )
        }

        // Build Track list for score
        val tracksList = mutableListOf<TabTrack>()
        tracksList.add(
            TabTrack(
                id = UUID.randomUUID().toString(),
                name = trackName.ifBlank { "Guitar" },
                instrumentType = instrumentType,
                stringCount = stringCount,
                stringLabels = stringLabels,
                tuningName = tuningDisplayName,
                tuningNotes = tuningNotes,
                volume = 1.0f,
                pan = 0.0f,
                measures = parsedMeasures
            )
        )

        // Also add metadata summaries for other tracks so mixer shows them
        for (t in 0 until metaTracks.length()) {
            if (t == activeTrackIdx) continue
            val trkObj = metaTracks.getJSONObject(t)
            val isVocal = trkObj.optBoolean("isVocalTrack", false)
            if (isVocal) continue

            val name = trkObj.optString("name", "Track ${t + 1}")
            val inst = trkObj.optString("instrument", "Guitar")
            val trkType = if (inst.contains("Bass", ignoreCase = true)) InstrumentType.BASS else InstrumentType.GUITAR
            tracksList.add(
                TabTrack(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    instrumentType = trkType,
                    measures = emptyList() // lazy loaded when selected
                )
            )
        }

        return TabScore(
            id = UUID.randomUUID().toString(),
            title = title,
            artist = artist,
            revisionDate = "Songsterr #$songId",
            tempo = tempoBpm,
            timeSignatureNumerator = currentNumerator,
            timeSignatureDenominator = currentDenominator,
            tracks = tracksList
        )
    }

    private fun midiToPitchName(midi: Int): String {
        val noteNames = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        val octave = (midi / 12) - 1
        val note = noteNames[(midi % 12 + 12) % 12]
        return "$note$octave"
    }

    private fun detectTuningName(midiNotes: List<Int>): String {
        return when (midiNotes) {
            listOf(64, 59, 55, 50, 45, 40) -> "Standard E"
            listOf(62, 59, 55, 50, 45, 38) -> "Drop D"
            listOf(60, 57, 53, 48, 43, 36) -> "Drop C"
            listOf(58, 55, 51, 46, 41, 34) -> "Drop Bb"
            listOf(62, 57, 53, 48, 43, 38) -> "D Standard"
            listOf(60, 55, 51, 46, 41, 36) -> "C Standard"
            listOf(63, 58, 54, 49, 44, 39) -> "Eb Standard"
            listOf(43, 38, 33, 28) -> "Standard Bass"
            listOf(43, 38, 33, 26) -> "Drop D Bass"
            listOf(43, 38, 33, 28, 23) -> "5-String Bass"
            else -> "Кастомный строй"
        }
    }
}
