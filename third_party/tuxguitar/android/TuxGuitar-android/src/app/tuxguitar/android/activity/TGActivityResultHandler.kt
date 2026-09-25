package app.tuxguitar.android.activity

import android.content.Intent

fun interface TGActivityResultHandler {
    fun onActivityResult(resultCode: Int, data: Intent?)
}
