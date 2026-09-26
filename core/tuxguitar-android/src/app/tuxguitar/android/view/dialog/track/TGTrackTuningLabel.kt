package app.tuxguitar.android.view.dialog.track

object TGTrackTuningLabel {
    @JvmField
    val KEY_NAMES = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    @JvmStatic
    fun valueOf(value: Int?): String = valueOf(value, false)

    @JvmStatic
    fun valueOf(value: Int?, octave: Boolean): String {
        if (value == null) return ""
        return buildString {
            append(KEY_NAMES[value % KEY_NAMES.size])
            if (octave) append(value / KEY_NAMES.size)
        }
    }
}
