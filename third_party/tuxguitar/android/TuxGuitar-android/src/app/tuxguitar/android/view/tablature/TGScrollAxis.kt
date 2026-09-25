package app.tuxguitar.android.view.tablature

class TGScrollAxis {
    private var enabledValue = false
    private var maximumValue = 0f
    private var minimumValue = 0f
    private var currentValue = 0f

    fun reset(enabled: Boolean, maximum: Float, minimum: Float, value: Float) {
        this.enabledValue = enabled
        this.maximumValue = maximum
        this.minimumValue = minimum
        this.currentValue = value
    }

    fun isEnabled(): Boolean = enabledValue
    fun setEnabled(enabled: Boolean) {
        this.enabledValue = enabled
    }

    fun getMaximum(): Float = maximumValue
    fun setMaximum(maximum: Float) {
        this.maximumValue = maximum
    }

    fun getMinimum(): Float = minimumValue
    fun setMinimum(minimum: Float) {
        this.minimumValue = minimum
    }

    fun getValue(): Float = currentValue
    fun setValue(value: Float) {
        this.currentValue = value
    }
}
