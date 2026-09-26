package com.mmt.guitarlab.data.repository

import com.mmt.guitarlab.audio.midi.MidiExporter
import com.mmt.guitarlab.data.db.TabProjectDao
import com.mmt.guitarlab.data.db.TabProjectEntity
import com.mmt.guitarlab.data.parser.TabParser
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.NoteDuration
import com.mmt.guitarlab.domain.model.NoteEffect
import com.mmt.guitarlab.domain.model.TabBeat
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.domain.model.TabNote
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import com.mmt.guitarlab.domain.repository.TabRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TabRepositoryImpl @Inject constructor(
    private val tabProjectDao: TabProjectDao,
) : TabRepository {

    override suspend fun parseTab(inputStream: InputStream, filename: String?): Result<TabScore> {
        return withContext(Dispatchers.IO) {
            runCatching {
                TabParser.parse(inputStream, filename)
            }
        }
    }

    override suspend fun parseAsciiText(text: String, title: String): Result<TabScore> {
        return withContext(Dispatchers.IO) {
            runCatching {
                TabParser.parseAsciiTab(text, title)
            }
        }
    }

    override suspend fun exportMidi(score: TabScore, outputFile: File): Result<File> {
        return withContext(Dispatchers.IO) {
            runCatching {
                MidiExporter.exportToMidi(score, outputFile)
            }
        }
    }

    override fun getAllProjects(): Flow<List<com.mmt.guitarlab.domain.repository.TabProjectInfo>> {
        return kotlinx.coroutines.flow.flow {
            tabProjectDao.getAllProjects().collect { list ->
                emit(list.map { com.mmt.guitarlab.domain.repository.TabProjectInfo(it.id, it.title, it.updatedAt) })
            }
        }
    }

    override suspend fun saveProject(score: TabScore): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val jsonStr = encodeScoreToJson(score)
                val entity = TabProjectEntity(
                    id = score.id,
                    title = score.title,
                    artist = score.artist,
                    updatedAt = System.currentTimeMillis(),
                    jsonContent = jsonStr,
                )
                tabProjectDao.insertProject(entity)
            }
        }
    }

    override suspend fun loadProject(id: String): Result<TabScore> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val entity = tabProjectDao.getProjectById(id) ?: throw IllegalArgumentException("Project not found")
                decodeJsonToScore(entity.jsonContent)
            }
        }
    }

    override suspend fun deleteProject(id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val entity = tabProjectDao.getProjectById(id)
                if (entity != null) {
                    tabProjectDao.deleteProject(entity)
                }
            }
        }
    }

    private fun encodeScoreToJson(score: TabScore): String {
        val root = JSONObject()
        root.put("id", score.id)
        root.put("title", score.title)
        root.put("artist", score.artist)
        root.put("tempo", score.tempo)
        root.put("tsNum", score.timeSignatureNumerator)
        root.put("tsDenom", score.timeSignatureDenominator)

        val tracksArray = JSONArray()
        score.tracks.forEach { track ->
            val trackObj = JSONObject()
            trackObj.put("id", track.id)
            trackObj.put("name", track.name)
            trackObj.put("instType", track.instrumentType.name)
            trackObj.put("strCount", track.stringCount)
            trackObj.put("volume", track.volume.toDouble())
            trackObj.put("pan", track.pan.toDouble())
            trackObj.put("midiProgram", track.midiProgram)
            trackObj.put("isMuted", track.isMuted)
            trackObj.put("isSolo", track.isSolo)

            val strLabelsArray = JSONArray()
            track.stringLabels.forEach { strLabelsArray.put(it) }
            trackObj.put("strLabels", strLabelsArray)

            val measuresArray = JSONArray()
            track.measures.forEach { measure ->
                val measureObj = JSONObject()
                measureObj.put("num", measure.number)
                measureObj.put("tsNum", measure.timeSignatureNumerator)
                measureObj.put("tsDenom", measure.timeSignatureDenominator)

                val beatsArray = JSONArray()
                measure.beats.forEach { beat ->
                    val beatObj = JSONObject()
                    beatObj.put("start", beat.startBeat.toDouble())
                    beatObj.put("dur", beat.durationBeats.toDouble())
                    beatObj.put("durType", beat.durationType.name)

                    val notesArray = JSONArray()
                    beat.notes.forEach { note ->
                        val noteObj = JSONObject()
                        noteObj.put("sIdx", note.stringIndex)
                        noteObj.put("fret", note.fret)
                        noteObj.put("fx", note.effect.name)
                        noteObj.put("vel", note.velocity)
                        notesArray.put(noteObj)
                    }
                    beatObj.put("notes", notesArray)
                    beatsArray.put(beatObj)
                }
                measureObj.put("beats", beatsArray)
                measuresArray.put(measureObj)
            }
            trackObj.put("measures", measuresArray)
            tracksArray.put(trackObj)
        }
        root.put("tracks", tracksArray)
        return root.toString()
    }

    private fun decodeJsonToScore(jsonStr: String): TabScore {
        val root = JSONObject(jsonStr)
        val id = root.optString("id")
        val title = root.optString("title", "Untitled")
        val artist = root.optString("artist", "Unknown")
        val tempo = root.optInt("tempo", 120)

        val tracksArray = root.optJSONArray("tracks") ?: JSONArray()
        val tracks = mutableListOf<TabTrack>()

        for (i in 0 until tracksArray.length()) {
            val tObj = tracksArray.getJSONObject(i)
            val tName = tObj.optString("name", "Guitar")
            val instTypeStr = tObj.optString("instType", InstrumentType.GUITAR.name)
            val instType = runCatching { InstrumentType.valueOf(instTypeStr) }.getOrDefault(InstrumentType.GUITAR)
            val strCount = tObj.optInt("strCount", instType.defaultStringCount)
            val volume = tObj.optDouble("volume", 1.0).toFloat()
            val pan = tObj.optDouble("pan", 0.0).toFloat()
            val midiProg = tObj.optInt("midiProgram", instType.defaultMidiProgram)
            val isMuted = tObj.optBoolean("isMuted", false)
            val isSolo = tObj.optBoolean("isSolo", false)

            val mArray = tObj.optJSONArray("measures") ?: JSONArray()
            val measures = mutableListOf<TabMeasure>()

            for (mIdx in 0 until mArray.length()) {
                val mObj = mArray.getJSONObject(mIdx)
                val mNum = mObj.optInt("num", mIdx + 1)
                val bArray = mObj.optJSONArray("beats") ?: JSONArray()
                val beats = mutableListOf<TabBeat>()

                for (bIdx in 0 until bArray.length()) {
                    val bObj = bArray.getJSONObject(bIdx)
                    val start = bObj.optDouble("start", 0.0).toFloat()
                    val dur = bObj.optDouble("dur", 0.5).toFloat()
                    val durTypeStr = bObj.optString("durType", NoteDuration.EIGHTH.name)
                    val durType = runCatching { NoteDuration.valueOf(durTypeStr) }.getOrDefault(NoteDuration.EIGHTH)

                    val nArray = bObj.optJSONArray("notes") ?: JSONArray()
                    val notes = mutableListOf<TabNote>()

                    for (nIdx in 0 until nArray.length()) {
                        val nObj = nArray.getJSONObject(nIdx)
                        val sIdx = nObj.optInt("sIdx", 0)
                        val fret = nObj.optInt("fret", 0)
                        val fxStr = nObj.optString("fx", NoteEffect.NONE.name)
                        val fx = runCatching { NoteEffect.valueOf(fxStr) }.getOrDefault(NoteEffect.NONE)
                        val vel = nObj.optInt("vel", 100)

                        notes.add(
                            TabNote(
                                stringIndex = sIdx,
                                fret = fret,
                                effect = fx,
                                startBeat = start,
                                durationBeats = dur,
                                velocity = vel,
                            ),
                        )
                    }
                    beats.add(TabBeat(notes = notes, startBeat = start, durationBeats = dur, durationType = durType))
                }
                measures.add(TabMeasure(number = mNum, beats = beats))
            }

            tracks.add(
                TabTrack(
                    id = tObj.optString("id"),
                    name = tName,
                    instrumentType = instType,
                    stringCount = strCount,
                    volume = volume,
                    pan = pan,
                    midiProgram = midiProg,
                    measures = measures,
                    isMuted = isMuted,
                    isSolo = isSolo,
                ),
            )
        }

        return TabScore(
            id = id,
            title = title,
            artist = artist,
            tempo = tempo,
            tracks = tracks,
        )
    }
}
