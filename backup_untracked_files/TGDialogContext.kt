package app.tuxguitar.android.view.dialog

class TGDialogContext {
    private val attributes = HashMap<String, Any?>()

    fun setAttribute(key: String, value: Any?) {
        attributes[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAttribute(key: String): T? = attributes[key] as T?
}
