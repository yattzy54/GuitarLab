package app.tuxguitar.android.properties

import android.content.Context
import android.content.SharedPreferences

abstract class TGSharedPreferencesHandler(
    private val context: Context,
    private val module: String,
    private val resource: String,
) {
    fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(TGSharedPreferencesUtil.getSharedPreferencesName(context, module, resource), 0)
    }
}
