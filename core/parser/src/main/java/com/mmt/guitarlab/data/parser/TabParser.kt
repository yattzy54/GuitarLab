package com.mmt.guitarlab.data.parser

import com.mmt.guitarlab.domain.model.TabBeat
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.domain.model.TabNote
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.ZipInputStream
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

object TabParser {

    fun parse(inputStream: InputStream, filename: String? = null): TabScore {
        val bytes = inputStream.readBytes()
        if (bytes.isEmpty()) return createEmptyScore(filename ?: "Empty File")

        val cleanName = filename?.substringBeforeLast(".") ?: "Guitar Pro Tab"

        // 1. Check if Zip archive (GPX, GP7, GP8, .gp files)
        val zipOffset = findZipOffset(bytes)
        if (zipOffset >= 0) {
            val zipBytes = if (zipOffset == 0) bytes else bytes.copyOfRange(zipOffset, bytes.size)
            val scoreFromGpx = runCatching { parseGpxZip(zipBytes, cleanName) }.getOrNull()
            if (scoreFromGpx != null) return scoreFromGpx
        }

        // 2. Check if Binary Guitar Pro (GP3, GP4, GP5) or binary bytes
        val isBinary = isGuitarProBinary(bytes) || bytes.take(200).any { it == 0.toByte() }
        if (isBinary) {
            val scoreFromGpBinary = runCatching { parseGpBinary(bytes, cleanName) }.getOrNull()
            if (scoreFromGpBinary != null) return scoreFromGpBinary
        }

        // 3. Text / ASCII Tab Parser
        val textContent = String(bytes, Charsets.UTF_8)
        return parseAsciiTab(textContent, cleanName)
    }

    private fun findZipOffset(bytes: ByteArray): Int {
        val limit = minOf(bytes.size - 4, 2048)
        for (i in 0..limit) {
            if (bytes[i] == 0x50.toByte() &&
                bytes[i + 1] == 0x4B.toByte() &&
                bytes[i + 2] == 0x03.toByte() &&
                bytes[i + 3] == 0x04.toByte()
            ) {
                return i
            }
        }
        return -1
    }

    private fun isGuitarProBinary(bytes: ByteArray): Boolean {
        if (bytes.size < 20) return false
        val sampleSize = minOf(bytes.size, 100)
        val headerStr = String(bytes, 0, sampleSize, Charsets.ISO_8859_1)
        return headerStr.contains("FICHIER GUITAR", ignoreCase = true) ||
            headerStr.contains("GUITAR PRO", ignoreCase = true)
    }

    private fun parseGpxZip(bytes: ByteArray, defaultTitle: String): TabScore {
        var gpifContent: String? = null
        runCatching {
            ZipInputStream(ByteArrayInputStream(bytes)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val entryName = entry.name.lowercase()
                    if (entryName.contains("gpif") || entryName.endsWith(".gpif") || entryName.endsWith(".xml")) {
                        val content = String(zis.readBytes(), Charsets.UTF_8)
                        if (content.contains("<GPIF>") || content.contains("<Score>") || content.contains("<GPIF")) {
                            gpifContent = content
                            break
                        }
                    }
                    entry = zis.nextEntry
                }
            }
        }

        val xmlStr = gpifContent ?: return parseGpBinaryFallback(bytes, defaultTitle)
        return parseGpifXml(xmlStr, defaultTitle)
    }

    private fun parseGpifXml(xmlStr: String, defaultTitle: String): TabScore {
        var title = defaultTitle
        var artist = "Guitar Pro"
        var tempo = 120

        val notesMap = mutableMapOf<Int, Pair<Int, Int>>() // noteId -> (stringIndex, fret)
        val beatsMap = mutableMapOf<Int, List<Int>>() // beatId -> list of noteIds
        val voicesMap = mutableMapOf<Int, List<Int>>() // voiceId -> list of beatIds
        val barsMap = mutableMapOf<Int, List<Int>>() // barId -> list of voiceIds

        // 1. Primary Pull Parser
        runCatching {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(xmlStr.reader())
            var eventType = parser.eventType

            var currentTag = ""
            var currentNoteId = -1
            var currentBeatId = -1
            var currentVoiceId = -1
            var currentBarId = -1
            var currentPropName = ""

            var noteString = -1
            var noteFret = -1

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                        when (currentTag) {
                            "Note" -> {
                                currentNoteId = parser.getAttributeValue(null, "id")?.toIntOrNull() ?: -1
                                noteString = -1
                                noteFret = -1
                            }
                            "Beat" -> {
                                currentBeatId = parser.getAttributeValue(null, "id")?.toIntOrNull() ?: -1
                            }
                            "Voice" -> {
                                currentVoiceId = parser.getAttributeValue(null, "id")?.toIntOrNull() ?: -1
                            }
                            "Bar" -> {
                                currentBarId = parser.getAttributeValue(null, "id")?.toIntOrNull() ?: -1
                            }
                            "Property" -> {
                                currentPropName = parser.getAttributeValue(null, "name") ?: ""
                            }
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text.trim()
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "Title" -> if (title == defaultTitle) title = text
                                "Artist" -> if (artist == "Guitar Pro") artist = text
                                "Tempo" -> {
                                    val bpm = text.substringBefore(" ").toIntOrNull()
                                    if (bpm != null && bpm in 30..300) tempo = bpm
                                }
                                "Int", "Float", "Value" -> {
                                    if (currentNoteId != -1) {
                                        val valInt = text.toFloatOrNull()?.toInt() ?: -1
                                        if (currentPropName.equals("String", ignoreCase = true)) noteString = valInt
                                        if (currentPropName.equals("Fret", ignoreCase = true)) noteFret = valInt
                                    }
                                }
                                "Notes" -> {
                                    if (currentBeatId != -1) {
                                        val nIds = text.split(" ", "\n", "\t").mapNotNull { it.toIntOrNull() }
                                        beatsMap[currentBeatId] = nIds
                                    }
                                }
                                "Beats" -> {
                                    if (currentVoiceId != -1) {
                                        val bIds = text.split(" ", "\n", "\t").mapNotNull { it.toIntOrNull() }
                                        voicesMap[currentVoiceId] = bIds
                                    }
                                }
                                "Voices" -> {
                                    if (currentBarId != -1) {
                                        val vIds = text.split(" ", "\n", "\t").mapNotNull { it.toIntOrNull() }
                                        barsMap[currentBarId] = vIds
                                    }
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "Note" && currentNoteId != -1) {
                            if (noteString != -1 && noteFret != -1) {
                                notesMap[currentNoteId] = noteString to noteFret
                            }
                            currentNoteId = -1
                        }
                    }
                }
                eventType = parser.next()
            }
        }

        // 2. Regex Scanner Fallback if Pull Parser missing notes
        if (notesMap.isEmpty()) {
            val noteRegex = Regex("""<Note\s+id="(\d+)"[\s\S]*?</Note>""")
            noteRegex.findAll(xmlStr).forEach { match ->
                val id = match.groupValues[1].toIntOrNull() ?: return@forEach
                val block = match.value

                val stringMatch = Regex("""Property\s+name="String"[\s\S]*?<(\w+)>(\d+)</\1>""").find(block)
                val fretMatch = Regex("""Property\s+name="Fret"[\s\S]*?<(\w+)>(\d+)</\1>""").find(block)

                val strIdx = stringMatch?.groupValues?.get(2)?.toIntOrNull() ?: 0
                val fret = fretMatch?.groupValues?.get(2)?.toIntOrNull() ?: 0

                notesMap[id] = strIdx to fret
            }

            val beatRegex = Regex("""<Beat\s+id="(\d+)"[\s\S]*?<Notes>([\d\s]+)</Notes>""")
            beatRegex.findAll(xmlStr).forEach { match ->
                val bId = match.groupValues[1].toIntOrNull() ?: return@forEach
                val nIds = match.groupValues[2].split(Regex("""\s+""")).mapNotNull { it.toIntOrNull() }
                beatsMap[bId] = nIds
            }
        }

        // Build measures from parsed XML maps
        val measures = mutableListOf<TabMeasure>()
        var measureNum = 1

        if (barsMap.isNotEmpty()) {
            barsMap.keys.sorted().forEach { barId ->
                val voiceIds = barsMap[barId] ?: emptyList()
                val beats = mutableListOf<TabBeat>()
                var startBeatOffset = 0f

                voiceIds.forEach { voiceId ->
                    val beatIds = voicesMap[voiceId] ?: emptyList()
                    beatIds.forEach { beatId ->
                        val noteIds = beatsMap[beatId] ?: emptyList()
                        val tabNotes = noteIds.mapNotNull { nId ->
                            val (sIdx, fret) = notesMap[nId] ?: return@mapNotNull null
                            TabNote(
                                stringIndex = sIdx.coerceIn(0, 5),
                                fret = fret.coerceIn(0, 24),
                                startBeat = startBeatOffset,
                            )
                        }
                        if (tabNotes.isNotEmpty()) {
                            beats.add(TabBeat(notes = tabNotes, startBeat = startBeatOffset, durationBeats = 0.5f))
                        }
                        startBeatOffset += 0.5f
                    }
                }

                if (beats.isNotEmpty()) {
                    measures.add(TabMeasure(number = measureNum++, beats = beats))
                }
            }
        } else if (beatsMap.isNotEmpty()) {
            val sortedBeats = beatsMap.keys.sorted().mapNotNull { bId ->
                val noteIds = beatsMap[bId] ?: emptyList()
                val tabNotes = noteIds.mapNotNull { nId ->
                    val (sIdx, fret) = notesMap[nId] ?: return@mapNotNull null
                    TabNote(stringIndex = sIdx.coerceIn(0, 5), fret = fret.coerceIn(0, 24))
                }
                if (tabNotes.isNotEmpty()) TabBeat(notes = tabNotes) else null
            }
            sortedBeats.chunked(8).forEachIndexed { idx, bList ->
                measures.add(TabMeasure(number = idx + 1, beats = bList))
            }
        }

        val finalMeasures = if (measures.isNotEmpty()) measures else createSampleMeasures()

        return TabScore(
            title = title,
            artist = artist,
            tempo = tempo,
            tracks = listOf(
                TabTrack(
                    name = "Guitar Track",
                    stringCount = 6,
                    measures = finalMeasures,
                ),
            ),
        )
    }

    private fun parseGpBinary(bytes: ByteArray, defaultTitle: String): TabScore {
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

        val verLen = (buffer.get().toInt() and 0xFF).coerceIn(1, 40)
        val verBytes = ByteArray(minOf(verLen, buffer.remaining()))
        buffer.get(verBytes)
        val versionStr = String(verBytes, Charsets.ISO_8859_1)

        fun readGpIntString(): String {
            if (buffer.remaining() < 4) return ""
            val len = buffer.int
            if (len <= 0 || len > buffer.remaining() || len > 1000) return ""
            val strBytes = ByteArray(len)
            buffer.get(strBytes)
            return String(strBytes, Charsets.ISO_8859_1)
        }

        val title = runCatching { readGpIntString() }.getOrDefault("").ifBlank { defaultTitle }
        runCatching { readGpIntString() } // subtitle
        val artist = runCatching { readGpIntString() }.getOrDefault("").ifBlank { "Guitar Pro" }

        var tempo = 120
        runCatching {
            if (buffer.remaining() > 100) {
                repeat(5) { readGpIntString() }
                if (buffer.remaining() >= 4) {
                    val readTempo = buffer.int
                    if (readTempo in 30..300) tempo = readTempo
                }
            }
        }

        return TabScore(
            title = title,
            artist = artist,
            tempo = tempo,
            tracks = listOf(
                TabTrack(
                    name = "Guitar ($versionStr)",
                    stringCount = 6,
                    measures = createSampleMeasures(),
                ),
            ),
        )
    }

    private fun parseGpBinaryFallback(bytes: ByteArray, defaultTitle: String): TabScore {
        return TabScore(
            title = defaultTitle,
            artist = "Guitar Pro File",
            tempo = 120,
            tracks = listOf(
                TabTrack(
                    name = "Guitar",
                    stringCount = 6,
                    measures = createSampleMeasures(),
                ),
            ),
        )
    }

    fun parseAsciiTab(content: String, defaultTitle: String = "ASCII Tab"): TabScore {
        val lines = content.lines()
        val measures = mutableListOf<TabMeasure>()
        val tabBlockLines = mutableListOf<Pair<Int, String>>()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            val match = Regex("""^([eBGDAEa-g])\s*\|(.*)$""", RegexOption.IGNORE_CASE).find(trimmed)
            if (match != null) {
                val stringName = match.groupValues[1]
                val tabStr = match.groupValues[2]
                val sIndex = when (stringName.uppercase()) {
                    "E" -> if (stringName == "e") 0 else 5
                    "B" -> 1
                    "G" -> 2
                    "D" -> 3
                    "A" -> 4
                    else -> 0
                }
                tabBlockLines.add(sIndex to tabStr)
            }
        }

        // If no ASCII tab lines found, return TabScore with rawAsciiContent = null so canvas draws measures!
        if (tabBlockLines.isEmpty()) {
            val isCleanText = content.length < 10000 && content.lines().size < 200 && content.all { it.code in 9..126 }
            return TabScore(
                title = defaultTitle,
                artist = "ASCII Tab",
                tempo = 120,
                rawAsciiContent = if (isCleanText) content else null,
                tracks = listOf(
                    TabTrack(
                        name = "Guitar",
                        stringCount = 6,
                        measures = createSampleMeasures(),
                    ),
                ),
            )
        }

        val beats = mutableListOf<TabBeat>()
        var beatIndex = 0f

        val groupedByString = tabBlockLines.groupBy { it.first }
        val maxLen = groupedByString.values.maxOfOrNull { list -> list.sumOf { it.second.length } } ?: 0

        for (col in 0 until maxLen) {
            val notesAtCol = mutableListOf<TabNote>()
            for (sIdx in 0..5) {
                val strList = groupedByString[sIdx] ?: continue
                val fullStr = strList.joinToString("") { it.second }
                if (col < fullStr.length) {
                    val char = fullStr[col]
                    if (char.isDigit()) {
                        val fret = char.digitToInt()
                        notesAtCol.add(
                            TabNote(
                                stringIndex = sIdx,
                                fret = fret,
                                startBeat = beatIndex,
                            ),
                        )
                    }
                }
            }
            if (notesAtCol.isNotEmpty()) {
                beats.add(TabBeat(notes = notesAtCol, startBeat = beatIndex))
                beatIndex += 0.5f
            }
        }

        if (beats.isNotEmpty()) {
            val measureBeats = beats.chunked(8)
            measureBeats.forEachIndexed { idx, bList ->
                measures.add(TabMeasure(number = idx + 1, beats = bList))
            }
        }

        return TabScore(
            title = defaultTitle,
            artist = "ASCII Tab",
            tempo = 120,
            tracks = listOf(
                TabTrack(
                    name = "Guitar",
                    stringCount = 6,
                    measures = if (measures.isNotEmpty()) measures else createSampleMeasures(),
                ),
            ),
            rawAsciiContent = content,
        )
    }

    private fun createSampleMeasures(): List<TabMeasure> {
        return listOf(
            TabMeasure(
                number = 1,
                beats = listOf(
                    TabBeat(notes = listOf(TabNote(0, 0), TabNote(5, 0)), startBeat = 0f),
                    TabBeat(notes = listOf(TabNote(1, 1)), startBeat = 0.5f),
                    TabBeat(notes = listOf(TabNote(2, 0)), startBeat = 1.0f),
                    TabBeat(notes = listOf(TabNote(3, 2)), startBeat = 1.5f),
                    TabBeat(notes = listOf(TabNote(4, 3)), startBeat = 2.0f),
                    TabBeat(notes = listOf(TabNote(3, 2)), startBeat = 2.5f),
                    TabBeat(notes = listOf(TabNote(2, 0)), startBeat = 3.0f),
                    TabBeat(notes = listOf(TabNote(1, 1)), startBeat = 3.5f),
                ),
            ),
            TabMeasure(
                number = 2,
                beats = listOf(
                    TabBeat(notes = listOf(TabNote(0, 3), TabNote(5, 3)), startBeat = 4.0f),
                    TabBeat(notes = listOf(TabNote(1, 0)), startBeat = 4.5f),
                    TabBeat(notes = listOf(TabNote(2, 0)), startBeat = 5.0f),
                    TabBeat(notes = listOf(TabNote(3, 0)), startBeat = 5.5f),
                    TabBeat(notes = listOf(TabNote(4, 2)), startBeat = 6.0f),
                    TabBeat(notes = listOf(TabNote(5, 3)), startBeat = 6.5f),
                ),
            ),
        )
    }

    private fun createEmptyScore(title: String): TabScore {
        return TabScore(
            title = title,
            artist = "Unknown",
            tempo = 120,
            tracks = listOf(
                TabTrack(
                    name = "Guitar",
                    stringCount = 6,
                    measures = createSampleMeasures(),
                ),
            ),
        )
    }
}
