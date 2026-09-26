package app.tuxguitar.android.activity

import android.content.Intent

class TGActivityResultManager {
    private var requestCode = 1
    private val handlers = mutableMapOf<Int, MutableList<TGActivityResultHandler>>()

    @Synchronized
    fun initialize() {
        requestCode = 1
        handlers.clear()
    }

    @Synchronized
    fun createRequestCode(): Int = requestCode++

    fun getHandlers(requestCode: Int): MutableList<TGActivityResultHandler> =
        handlers.getOrPut(requestCode) { mutableListOf() }

    fun addHandler(requestCode: Int, handler: TGActivityResultHandler) {
        if (handler !in getHandlers(requestCode)) getHandlers(requestCode).add(handler)
    }

    fun removeHandler(requestCode: Int, handler: TGActivityResultHandler) {
        getHandlers(requestCode).remove(handler)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        getHandlers(requestCode).toList().forEach { it.onActivityResult(resultCode, data) }
    }
}
