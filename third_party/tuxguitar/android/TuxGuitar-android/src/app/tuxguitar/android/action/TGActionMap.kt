package app.tuxguitar.android.action

class TGActionMap<T> {
    private val map = HashMap<String, T>()

    fun set(actionId: String, value: T?) {
        if (value != null) {
            map[actionId] = value
        } else {
            map.remove(actionId)
        }
    }

    fun get(actionId: String): T? = map[actionId]
}
