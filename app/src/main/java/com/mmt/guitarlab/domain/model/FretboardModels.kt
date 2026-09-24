package com.mmt.guitarlab.domain.model

enum class FretboardMode {
    CHORD_SCALE_FINDER,
    REVERSE_LOOKUP
}

data class ChordFormula(
    val name: String,
    val intervals: List<Int>, // semitones relative to root
)

data class ScaleFormula(
    val name: String,
    val intervals: List<Int>,
)

object MusicTheory {
    val noteNames = listOf("C", "C♯", "D", "D♯", "E", "F", "F♯", "G", "G♯", "A", "A♯", "B")

    val chordFormulas = listOf(
        ChordFormula("Major", listOf(0, 4, 7)),
        ChordFormula("Minor", listOf(0, 3, 7)),
        ChordFormula("5 (Power Chord)", listOf(0, 7)),
        ChordFormula("7 (Dominant)", listOf(0, 4, 7, 10)),
        ChordFormula("maj7", listOf(0, 4, 7, 11)),
        ChordFormula("m7", listOf(0, 3, 7, 10)),
        ChordFormula("sus4", listOf(0, 5, 7)),
        ChordFormula("sus2", listOf(0, 2, 7)),
        ChordFormula("diminished", listOf(0, 3, 6)),
        ChordFormula("augmented", listOf(0, 4, 8)),
        ChordFormula("add9", listOf(0, 2, 4, 7)),
        ChordFormula("m9", listOf(0, 2, 3, 7, 10)),
        ChordFormula("9", listOf(0, 2, 4, 7, 10)),
        ChordFormula("6", listOf(0, 4, 7, 9)),
        ChordFormula("m6", listOf(0, 3, 7, 9)),
    )

    val scaleFormulas = listOf(
        ScaleFormula("Minor Pentatonic", listOf(0, 3, 5, 7, 10)),
        ScaleFormula("Major Pentatonic", listOf(0, 2, 4, 7, 9)),
        ScaleFormula("Blues Scale", listOf(0, 3, 5, 6, 7, 10)),
        ScaleFormula("Major (Ionian)", listOf(0, 2, 4, 5, 7, 9, 11)),
        ScaleFormula("Natural Minor (Aeolian)", listOf(0, 2, 3, 5, 7, 8, 10)),
        ScaleFormula("Dorian", listOf(0, 2, 3, 5, 7, 9, 10)),
        ScaleFormula("Mixolydian", listOf(0, 2, 4, 5, 7, 9, 10)),
    )

    fun getMidiNoteIndex(noteName: String): Int {
        val index = noteNames.indexOf(noteName)
        return if (index != -1) index else 0
    }

    /**
     * Accurate reverse chord detection:
     * Prioritizes exact chord matches where all played pitch classes match the chord formula.
     * Secondary matches include extensions or root inversions with lowest bass note notation (e.g. C/E).
     */
    fun reverseLookupChord(selectedMidiNotes: List<Int>): List<String> {
        if (selectedMidiNotes.isEmpty()) return emptyList()

        // Lowest note determines the bass note
        val lowestMidi = selectedMidiNotes.minOrNull() ?: 0
        val bassPitch = (lowestMidi % 12 + 12) % 12
        val bassName = noteNames[bassPitch]

        val pitchClasses = selectedMidiNotes.map { (it % 12 + 12) % 12 }.distinct().toSet()
        if (pitchClasses.isEmpty()) return emptyList()

        val exactMatches = mutableListOf<String>()
        val partialMatches = mutableListOf<String>()

        for (rootPitch in pitchClasses) {
            val rootName = noteNames[rootPitch]
            val intervals = pitchClasses.map { (it - rootPitch + 12) % 12 }.toSet()

            for (formula in chordFormulas) {
                val formulaSet = formula.intervals.toSet()

                // Check exact match (all formula notes present, and no extraneous notes)
                if (formulaSet == intervals) {
                    val chordTitle = if (bassPitch != rootPitch && formula.intervals.size > 2) {
                        "$rootName ${formula.name}/$bassName"
                    } else {
                        "$rootName ${formula.name}"
                    }
                    exactMatches.add(chordTitle)
                } else if (formulaSet.all { intervals.contains(it) }) {
                    // Formula notes are present, but extra notes exist (extensions or compound chords)
                    val chordTitle = if (bassPitch != rootPitch && formula.intervals.size > 2) {
                        "$rootName ${formula.name}/$bassName"
                    } else {
                        "$rootName ${formula.name}"
                    }
                    partialMatches.add(chordTitle)
                }
            }
        }

        val combined = if (exactMatches.isNotEmpty()) {
            exactMatches
        } else {
            partialMatches
        }

        return combined.distinct()
    }
}
