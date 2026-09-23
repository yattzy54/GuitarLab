package com.mmt.guitarlab.domain.model

data class DetectedPitch(
    val frequencyHz: Float,
    val midiNote: Int,
    val noteName: String,
    val octave: Int,
    val cents: Float,
    val clarity: Float,
) {
    val inTune: Boolean get() = kotlin.math.abs(cents) <= 5f
}

object PitchMath {
    private val names = arrayOf("C", "C♯", "D", "D♯", "E", "F", "F♯", "G", "G♯", "A", "A♯", "B")

    fun fromFrequency(frequencyHz: Float, a4Hz: Float, clarity: Float): DetectedPitch? {
        if (frequencyHz <= 0f || !frequencyHz.isFinite()) return null
        val midi = 69.0 + 12.0 * (kotlin.math.ln((frequencyHz / a4Hz).toDouble()) / kotlin.math.ln(2.0))
        val nearest = midi.roundToIntSafe()
        val cents = ((midi - nearest) * 100.0).toFloat()
        val noteIndex = ((nearest % 12) + 12) % 12
        val octave = nearest / 12 - 1
        return DetectedPitch(
            frequencyHz = frequencyHz,
            midiNote = nearest,
            noteName = names[noteIndex],
            octave = octave,
            cents = cents,
            clarity = clarity,
        )
    }

    private fun Double.roundToIntSafe(): Int =
        kotlin.math.round(this).toInt().coerceIn(0, 127)
}
