package app.tuxguitar.android.properties

import android.app.Activity
import app.tuxguitar.util.properties.TGProperties
import app.tuxguitar.util.properties.TGPropertiesException
import app.tuxguitar.util.properties.TGPropertiesWriter

class TGSharedPreferencesWriter(activity: Activity, module: String, resource: String) :
    TGSharedPreferencesHandler(activity, module, resource),
    TGPropertiesWriter {
    @Throws(TGPropertiesException::class)
    override fun writeProperties(properties: TGProperties, module: String) {
        val map = (properties as TGPropertiesImpl).getMap()
        val editor = getSharedPreferences().edit()
        for ((key, value) in map) {
            editor.putString(key, value)
        }
        editor.commit()
    }
}
