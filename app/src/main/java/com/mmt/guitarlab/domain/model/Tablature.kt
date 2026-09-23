package com.mmt.guitarlab.domain.model

import java.util.UUID

enum class NoteEffect(val symbol: String, val displayName: String) {
    NONE("", "Normal"),
    SLIDE("/", "Slide"),
    BEND("b", "Bend"),
    HAMMER_ON("h", "Hammer-On"),
    PULL_OFF("p", "Pull-Off"),
    PALM_MUTE("pm", "Palm Mute"),
    VIBRATO("~", "Vibrato"),
    LET_RING("lr", "Let Ring"),
    DEAD_NOTE("x", "Muted Note"),
}

enum class NoteDuration(val durationBeats: Float, val label: String) {
    WHOLE(4.0f, "1/1"),
    HALF(2.0f, "1/2"),
    QUARTER(1.0f, "1/4"),
    EIGHTH(0.5f, "1/8"),
    SIXTEENTH(0.25f, "1/16"),
    THIRTY_SECOND(0.125f, "1/32"),
}

enum class InstrumentType(
    val displayName: String,
    val defaultStringCount: Int,
    val defaultStringLabels: List<String>,
    val defaultMidiProgram: Int,
) {
    GUITAR("Guitar (6-Str)", 6, listOf("e", "B", "G", "D", "A", "E"), 25),
    GUITAR_7("Guitar (7-Str)", 7, listOf("e", "B", "G", "D", "A", "E", "B"), 25),
    GUITAR_8("Guitar (8-Str)", 8, listOf("e", "B", "G", "D", "A", "E", "B", "F♯"), 30),
    BASS("Bass (4-Str)", 4, listOf("G", "D", "A", "E"), 33),
    BASS_5("Bass (5-Str)", 5, listOf("G", "D", "A", "E", "B"), 33),
    UKULELE("Ukulele", 4, listOf("A", "E", "C", "G"), 24),
    DRUMS("Drums", 5, listOf("CC", "HH", "SD", "TM", "BD"), 0),
    KEYBOARD("Keys / Piano", 6, listOf("C5", "G4", "E4", "C4", "G3", "C3"), 0),
}

data class TabNote(
    val stringIndex: Int,       // 0 = highest pitch string
    val fret: Int,              // 0..24
    val effect: NoteEffect = NoteEffect.NONE,
    val startBeat: Float = 0f,
    val durationBeats: Float = 0.5f,
    val velocity: Int = 100,    // 0..127
) {
    val displayLabel: String
        get() = when (effect) {
            NoteEffect.DEAD_NOTE -> "x"
            NoteEffect.NONE -> fret.toString()
            else -> "$fret${effect.symbol}"
        }
}

data class TabBeat(
    val notes: List<TabNote> = emptyList(),
    val startBeat: Float = 0f,
    val durationBeats: Float = 0.5f,
    val durationType: NoteDuration = NoteDuration.EIGHTH,
)

data class TabMeasure(
    val number: Int,
    val timeSignatureNumerator: Int = 4,
    val timeSignatureDenominator: Int = 4,
    val tempoBpm: Int? = null,
    val beats: List<TabBeat> = emptyList(),
)

data class TabTrack(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Guitar",
    val instrumentType: InstrumentType = InstrumentType.GUITAR,
    val stringCount: Int = instrumentType.defaultStringCount,
    val stringLabels: List<String> = instrumentType.defaultStringLabels,
    val volume: Float = 1.0f,    // 0.0f..1.0f
    val pan: Float = 0.0f,       // -1.0f..1.0f
    val midiProgram: Int = instrumentType.defaultMidiProgram,
    val measures: List<TabMeasure> = emptyList(),
    val isMuted: Boolean = false,
    val isSolo: Boolean = false,
)

data class TabScore(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Untitled Project",
    val artist: String = "Unknown Artist",
    val tempo: Int = 120,
    val timeSignatureNumerator: Int = 4,
    val timeSignatureDenominator: Int = 4,
    val tracks: List<TabTrack> = emptyList(),
    val rawAsciiContent: String? = null,
)
