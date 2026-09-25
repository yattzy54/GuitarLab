package app.tuxguitar.android.properties

import android.app.Activity
import android.content.SharedPreferences

abstract class TGSharedPreferencesHandler(
    private val activity: Activity,
    private val module: String,
    private val resource: String,
) {
    fun getSharedPreferences(): SharedPreferences {
        return activity.getSharedPreferences(TGSharedPreferencesUtil.getSharedPreferencesName(activity, module, resource), 0)
    }
}
