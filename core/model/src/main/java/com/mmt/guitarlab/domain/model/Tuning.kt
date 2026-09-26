package com.mmt.guitarlab.domain.model

data class TuningNote(
    val stringNumber: Int, // 1-indexed (1 is high E, N is lowest string)
    val noteName: String,  // e.g. "E", "A", "D"
    val octave: Int,        // e.g. 2, 3, 4
    val targetFrequencyHz: Float,
    val midiNote: Int,
) {
    val displayLabel: String get() = "$noteName$octave"
}

data class Tuning(
    val id: String,
    val name: String,
    val category: String, // "Standard", "Drop", "Open", "Alternate", "7-String", "8-String"
    val stringCount: Int,
    val notes: List<TuningNote>,
    val isFavorite: Boolean = false,
)
