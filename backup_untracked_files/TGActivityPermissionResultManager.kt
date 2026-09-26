package app.tuxguitar.android.activity

class TGActivityPermissionResultManager {
    private var requestCode = 1
    private val handlers = mutableMapOf<Int, MutableList<TGActivityPermissionResultHandler>>()

    @Synchronized
    fun initialize() {
        requestCode = 1
        handlers.clear()
    }

    @Synchronized
    fun createRequestCode(): Int = requestCode++

    fun getHandlers(requestCode: Int): MutableList<TGActivityPermissionResultHandler> =
        handlers.getOrPut(requestCode) { mutableListOf() }

    fun addHandler(requestCode: Int, handler: TGActivityPermissionResultHandler) {
        if (handler !in getHandlers(requestCode)) getHandlers(requestCode).add(handler)
    }

    fun removeHandler(requestCode: Int, handler: TGActivityPermissionResultHandler) {
        getHandlers(requestCode).remove(handler)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        getHandlers(requestCode).toList().forEach {
            it.onRequestPermissionsResult(permissions, grantResults)
        }
    }
}
