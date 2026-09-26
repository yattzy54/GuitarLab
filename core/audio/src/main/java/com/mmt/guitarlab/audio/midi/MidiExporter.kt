package com.mmt.guitarlab.audio.midi

import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.TabScore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object MidiExporter {

    private const val TICKS_PER_QUARTER = 480

    private val guitarPitches = intArrayOf(64, 59, 55, 50, 45, 40, 35, 30) // 8-string to 6-string
    private val bassPitches = intArrayOf(43, 38, 33, 28, 23) // 5-string to 4-string bass
    private val ukulelePitches = intArrayOf(69, 64, 60, 67) // A4, E4, C4, G4

    fun exportToMidi(score: TabScore, outputFile: File): File {
        val bytes = createMidiBytes(score)
        FileOutputStream(outputFile).use { fos ->
            fos.write(bytes)
        }
        return outputFile
    }

    fun createMidiBytes(score: TabScore): ByteArray {
        val stream = ByteArrayOutputStream()

        stream.write("MThd".toByteArray(Charsets.US_ASCII))
        write32Bit(stream, 6)
        write16Bit(stream, 0) // Format 0
        write16Bit(stream, 1)
        write16Bit(stream, TICKS_PER_QUARTER)

        val trackEvents = mutableListOf<MidiEvent>()

        val bpm = score.tempo.coerceIn(30, 300)
        val tempoMicroseconds = (60_000_000 / bpm)
        trackEvents.add(
            MidiEvent(
                tick = 0,
                bytes = byteArrayOf(
                    0xFF.toByte(), 0x51.toByte(), 0x03.toByte(),
                    ((tempoMicroseconds shr 16) and 0xFF).toByte(),
                    ((tempoMicroseconds shr 8) and 0xFF).toByte(),
                    (tempoMicroseconds and 0xFF).toByte(),
                ),
            ),
        )

        score.tracks.forEachIndexed { tIdx, track ->
            val channel = if (track.instrumentType == InstrumentType.DRUMS) 9 else (tIdx % 15).let { if (it >= 9) it + 1 else it }

            // Program Change Event
            val prog = track.midiProgram.coerceIn(0, 127)
            trackEvents.add(
                MidiEvent(
                    tick = 0,
                    bytes = byteArrayOf((0xC0 or channel).toByte(), prog.toByte()),
                ),
            )

            track.measures.forEach { measure ->
                measure.beats.forEach { beat ->
                    beat.notes.forEach { note ->
                        val pitch = when (track.instrumentType) {
                            InstrumentType.DRUMS -> when (note.stringIndex) {
                                0 -> 49 // Crash
                                1 -> 42 // Hi-Hat
                                2 -> 38 // Snare
                                3 -> 45 // Tom
                                else -> 36 // Kick
                            }
                            InstrumentType.BASS, InstrumentType.BASS_5 -> {
                                val base = bassPitches.getOrElse(note.stringIndex) { 28 }
                                (base + note.fret).coerceIn(0, 127)
                            }
                            InstrumentType.UKULELE -> {
                                val base = ukulelePitches.getOrElse(note.stringIndex) { 60 }
                                (base + note.fret).coerceIn(0, 127)
                            }
                            else -> {
                                val base = guitarPitches.getOrElse(note.stringIndex) { 40 }
                                (base + note.fret).coerceIn(0, 127)
                            }
                        }

                        val startTick = (beat.startBeat * TICKS_PER_QUARTER).toLong()
                        val durationTicks = (beat.durationBeats * TICKS_PER_QUARTER).toLong()
                        val endTick = startTick + durationTicks.coerceAtLeast(120)

                        // Note On
                        trackEvents.add(
                            MidiEvent(
                                tick = startTick,
                                bytes = byteArrayOf((0x90 or channel).toByte(), pitch.toByte(), note.velocity.coerceIn(1, 127).toByte()),
                            ),
                        )
                        // Note Off
                        trackEvents.add(
                            MidiEvent(
                                tick = endTick,
                                bytes = byteArrayOf((0x80 or channel).toByte(), pitch.toByte(), 0.toByte()),
                            ),
                        )
                    }
                }
            }
        }

        trackEvents.sortBy { it.tick }

        val maxTick = trackEvents.maxOfOrNull { it.tick } ?: 0L
        trackEvents.add(
            MidiEvent(
                tick = maxTick + 240,
                bytes = byteArrayOf(0xFF.toByte(), 0x2F.toByte(), 0x00.toByte()),
            ),
        )

        val trackData = ByteArrayOutputStream()
        var currentTick = 0L

        trackEvents.forEach { event ->
            val delta = event.tick - currentTick
            currentTick = event.tick
            writeVarLen(trackData, delta)
            trackData.write(event.bytes)
        }

        val trackBytes = trackData.toByteArray()

        stream.write("MTrk".toByteArray(Charsets.US_ASCII))
        write32Bit(stream, trackBytes.size)
        stream.write(trackBytes)

        return stream.toByteArray()
    }

    private data class MidiEvent(val tick: Long, val bytes: ByteArray)

    private fun write16Bit(stream: ByteArrayOutputStream, value: Int) {
        stream.write((value shr 8) and 0xFF)
        stream.write(value and 0xFF)
    }

    private fun write32Bit(stream: ByteArrayOutputStream, value: Int) {
        stream.write((value shr 24) and 0xFF)
        stream.write((value shr 16) and 0xFF)
        stream.write((value shr 8) and 0xFF)
        stream.write(value and 0xFF)
    }

    private fun writeVarLen(stream: ByteArrayOutputStream, value: Long) {
        var buffer = value and 0x7F
        var v = value shr 7
        val bytes = mutableListOf<Byte>()
        bytes.add(buffer.toByte())

        while (v > 0) {
            buffer = (v and 0x7F) or 0x80
            bytes.add(0, buffer.toByte())
            v = v shr 7
        }

        bytes.forEach { stream.write(it.toInt() and 0xFF) }
    }
}
