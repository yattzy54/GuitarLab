package com.mmt.guitarlab.ui.guitartabedit

import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.NoteDuration
import com.mmt.guitarlab.domain.model.NoteEffect
import com.mmt.guitarlab.domain.model.TabBeat
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.domain.model.TabNote
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import com.mmt.guitarlab.domain.model.TuxGuitarSoundBank

/**
 * Display modes for TuxGuitar tablature view.
 */
enum class TGViewMode(val title: String) {
    TAB_ONLY("TAB"),
    SCORE_ONLY("SCORE"),
    DUAL("DUAL")
}

/**
 * Tuning preset modeled after TuxGuitar's official tunings.xml.
 */
data class TGTuningPreset(
    val name: String,
    val group: String,
    val stringCount: Int,
    val labels: List<String>,
    val midiNotes: List<Int>
)

object TGTuningPresets {
    val presets = listOf(
        // 6-String Guitar
        TGTuningPreset("E Tuning (Standard)", "Guitar 6-String", 6, listOf("e", "B", "G", "D", "A", "E"), listOf(64, 59, 55, 50, 45, 40)),
        TGTuningPreset("Dropped D", "Guitar 6-String", 6, listOf("e", "B", "G", "D", "A", "D"), listOf(64, 59, 55, 50, 45, 38)),
        TGTuningPreset("D# Tuning (Half Step Down)", "Guitar 6-String", 6, listOf("d#", "A#", "F#", "C#", "G#", "D#"), listOf(63, 58, 54, 49, 44, 39)),
        TGTuningPreset("D Tuning (Full Step Down)", "Guitar 6-String", 6, listOf("d", "A", "F", "C", "G", "D"), listOf(62, 57, 53, 48, 43, 38)),
        TGTuningPreset("Dropped C", "Guitar 6-String", 6, listOf("d", "A", "F", "C", "G", "C"), listOf(62, 57, 53, 48, 43, 36)),
        TGTuningPreset("Dropped B", "Guitar 6-String", 6, listOf("c#", "G#", "E", "B", "F#", "B"), listOf(61, 56, 52, 47, 42, 35)),
        TGTuningPreset("DADGAD", "Guitar 6-String", 6, listOf("d", "A", "G", "D", "A", "D"), listOf(62, 57, 55, 50, 45, 38)),
        TGTuningPreset("Open D", "Guitar 6-String", 6, listOf("d", "A", "F#", "D", "A", "D"), listOf(62, 57, 54, 50, 45, 38)),
        TGTuningPreset("Open G", "Guitar 6-String", 6, listOf("d", "B", "G", "D", "G", "D"), listOf(62, 59, 55, 50, 43, 38)),
        TGTuningPreset("Open E", "Guitar 6-String", 6, listOf("e", "B", "G#", "E", "B", "E"), listOf(64, 59, 56, 52, 47, 40)),

        // 7-String Guitar
        TGTuningPreset("B Tuning (Standard 7)", "Guitar 7-String", 7, listOf("e", "B", "G", "D", "A", "E", "B"), listOf(64, 59, 55, 50, 45, 40, 35)),
        TGTuningPreset("Dropped A", "Guitar 7-String", 7, listOf("e", "B", "G", "D", "A", "E", "A"), listOf(64, 59, 55, 50, 45, 40, 33)),

        // 4-String Bass
        TGTuningPreset("E Tuning (Bass 4)", "Bass", 4, listOf("G", "D", "A", "E"), listOf(43, 38, 33, 28)),
        TGTuningPreset("Dropped D (Bass 4)", "Bass", 4, listOf("G", "D", "A", "D"), listOf(43, 38, 33, 26)),

        // 5-String Bass
        TGTuningPreset("B Tuning (Bass 5)", "Bass", 5, listOf("G", "D", "A", "E", "B"), listOf(43, 38, 33, 28, 23)),

        // Ukulele
        TGTuningPreset("Standard (GCEA)", "Ukulele", 4, listOf("A", "E", "C", "G"), listOf(69, 64, 60, 67))
    )
}

/**
 * Caret position in TuxGuitar editor.
 */
data class TGCaretState(
    val trackIndex: Int = 0,
    val measureIndex: Int = 0,
    val beatIndex: Int = 0,
    val stringIndex: Int = 0,
    val voice: Int = 0
)

/**
 * Demo scores library ported from TuxGuitar community and examples.
 */
object TGDemoSongs {
    fun createTuxGuitarTheme(): TabScore {
        val beats1 = listOf(
            TabBeat(notes = listOf(TabNote(stringIndex = 4, fret = 7, effect = NoteEffect.NONE, startBeat = 0.0f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 3, fret = 9, effect = NoteEffect.NONE, startBeat = 0.5f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 2, fret = 9, effect = NoteEffect.VIBRATO, startBeat = 1.0f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            TabBeat(notes = listOf(TabNote(stringIndex = 3, fret = 9, effect = NoteEffect.NONE, startBeat = 2.0f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 2, fret = 7, effect = NoteEffect.HAMMER_ON, startBeat = 2.5f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 2, fret = 9, effect = NoteEffect.LET_RING, startBeat = 3.0f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
        )
        val beats2 = listOf(
            TabBeat(notes = listOf(TabNote(stringIndex = 1, fret = 8, effect = NoteEffect.BEND, startBeat = 0.0f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 1, fret = 10, effect = NoteEffect.VIBRATO, startBeat = 0.5f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            TabBeat(notes = listOf(TabNote(stringIndex = 2, fret = 9, effect = NoteEffect.NONE, startBeat = 1.5f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 3, fret = 9, effect = NoteEffect.PALM_MUTE, startBeat = 2.0f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 3, fret = 7, effect = NoteEffect.SLIDE, startBeat = 2.5f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 4, fret = 7, effect = NoteEffect.LET_RING, startBeat = 3.0f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
        )
        val beats3 = listOf(
            TabBeat(notes = listOf(TabNote(stringIndex = 5, fret = 0, effect = NoteEffect.NONE, startBeat = 0.0f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 5, fret = 3, effect = NoteEffect.HAMMER_ON, startBeat = 0.5f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 4, fret = 2, effect = NoteEffect.NONE, startBeat = 1.0f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            TabBeat(notes = listOf(TabNote(stringIndex = 3, fret = 2, effect = NoteEffect.NONE, startBeat = 2.0f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            TabBeat(notes = listOf(TabNote(stringIndex = 2, fret = 0, effect = NoteEffect.LET_RING, startBeat = 3.0f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
        )
        val beats4 = listOf(
            TabBeat(notes = listOf(TabNote(stringIndex = 5, fret = 5, effect = NoteEffect.PALM_MUTE, startBeat = 0.0f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 5, fret = 5, effect = NoteEffect.PALM_MUTE, startBeat = 0.5f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 4, fret = 7, effect = NoteEffect.NONE, startBeat = 1.0f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 3, fret = 7, effect = NoteEffect.VIBRATO, startBeat = 1.5f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 4, fret = 5, effect = NoteEffect.SLIDE, startBeat = 2.0f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            TabBeat(notes = listOf(TabNote(stringIndex = 5, fret = 7, effect = NoteEffect.LET_RING, startBeat = 3.0f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
        )

        val measuresLead = listOf(
            TabMeasure(number = 1, tempoBpm = 120, beats = beats1),
            TabMeasure(number = 2, tempoBpm = 120, beats = beats2),
            TabMeasure(number = 3, tempoBpm = 120, beats = beats3),
            TabMeasure(number = 4, tempoBpm = 120, beats = beats4),
        )

        val bassBeats1 = listOf(
            TabBeat(notes = listOf(TabNote(stringIndex = 3, fret = 0, startBeat = 0.0f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            TabBeat(notes = listOf(TabNote(stringIndex = 3, fret = 0, startBeat = 1.0f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            TabBeat(notes = listOf(TabNote(stringIndex = 2, fret = 2, startBeat = 2.0f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            TabBeat(notes = listOf(TabNote(stringIndex = 1, fret = 2, startBeat = 3.0f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
        )
        val bassMeasures = listOf(
            TabMeasure(number = 1, beats = bassBeats1),
            TabMeasure(number = 2, beats = bassBeats1),
            TabMeasure(number = 3, beats = bassBeats1),
            TabMeasure(number = 4, beats = bassBeats1),
        )

        val trackLead = TabTrack(
            name = "Lead Guitar",
            instrumentType = InstrumentType.GUITAR,
            stringCount = 6,
            stringLabels = listOf("e", "B", "G", "D", "A", "E"),
            tuningName = "Standard E",
            tuningNotes = listOf("E4", "B3", "G3", "D3", "A2", "E2"),
            volume = 0.9f,
            pan = 0.0f,
            measures = measuresLead
        )

        val trackBass = TabTrack(
            name = "Electric Bass",
            instrumentType = InstrumentType.BASS,
            stringCount = 4,
            stringLabels = listOf("G", "D", "A", "E"),
            tuningName = "Standard Bass",
            tuningNotes = listOf("G2", "D2", "A1", "E1"),
            volume = 0.85f,
            pan = 0.0f,
            measures = bassMeasures
        )

        return TabScore(
            title = "TuxGuitar Anthem",
            artist = "Helge & TuxGuitar Team",
            tempo = 120,
            timeSignatureNumerator = 4,
            timeSignatureDenominator = 4,
            tracks = listOf(trackLead, trackBass)
        )
    }

    fun createRockRiff(): TabScore {
        val beats = listOf(
            TabBeat(notes = listOf(TabNote(stringIndex = 5, fret = 0, startBeat = 0.0f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 5, fret = 3, effect = NoteEffect.HAMMER_ON, startBeat = 0.5f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 5, fret = 5, effect = NoteEffect.VIBRATO, startBeat = 1.0f, durationBeats = 1.0f)), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            TabBeat(notes = listOf(TabNote(stringIndex = 5, fret = 0, startBeat = 2.0f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 5, fret = 3, effect = NoteEffect.HAMMER_ON, startBeat = 2.5f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 5, fret = 6, effect = NoteEffect.BEND, startBeat = 3.0f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
            TabBeat(notes = listOf(TabNote(stringIndex = 5, fret = 5, effect = NoteEffect.LET_RING, startBeat = 3.5f, durationBeats = 0.5f)), durationBeats = 0.5f, durationType = NoteDuration.EIGHTH),
        )
        val measures = listOf(
            TabMeasure(number = 1, tempoBpm = 110, beats = beats),
            TabMeasure(number = 2, tempoBpm = 110, beats = beats),
        )
        val track = TabTrack(
            name = "Rhythm Guitar",
            instrumentType = InstrumentType.GUITAR,
            stringCount = 6,
            stringLabels = listOf("e", "B", "G", "D", "A", "E"),
            tuningName = "Standard E",
            measures = measures
        )
        return TabScore(
            title = "Smoke on the Water Riff",
            artist = "Deep Purple",
            tempo = 110,
            timeSignatureNumerator = 4,
            timeSignatureDenominator = 4,
            tracks = listOf(track)
        )
    }

    fun createBlankScore(): TabScore {
        val emptyBeats = listOf(
            TabBeat(notes = emptyList(), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            TabBeat(notes = emptyList(), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            TabBeat(notes = emptyList(), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
            TabBeat(notes = emptyList(), durationBeats = 1.0f, durationType = NoteDuration.QUARTER),
        )
        val measures = listOf(
            TabMeasure(number = 1, beats = emptyBeats),
            TabMeasure(number = 2, beats = emptyBeats),
            TabMeasure(number = 3, beats = emptyBeats),
            TabMeasure(number = 4, beats = emptyBeats),
        )
        val track = TabTrack(
            name = "Acoustic Guitar",
            instrumentType = InstrumentType.GUITAR,
            stringCount = 6,
            stringLabels = listOf("e", "B", "G", "D", "A", "E"),
            tuningName = "Standard E",
            volume = 0.9f,
            measures = measures
        )
        return TabScore(
            title = "Untitled Tab",
            artist = "GuitarTabEdit",
            tempo = 120,
            timeSignatureNumerator = 4,
            timeSignatureDenominator = 4,
            tracks = listOf(track)
        )
    }
}
