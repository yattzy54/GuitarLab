package app.tuxguitar.android.view.tablature

class TGScrollAxis {
    var isEnabled = false
    var maximum = 0f
    var minimum = 0f
    var value = 0f

    fun reset(enabled: Boolean, maximum: Float, minimum: Float, value: Float) {
        this.isEnabled = enabled
        this.maximum = maximum
        this.minimum = minimum
        this.value = value
    }
}
