package app.tuxguitar.android.properties

import android.content.Context
import app.tuxguitar.util.properties.TGProperties
import app.tuxguitar.util.properties.TGPropertiesException
import app.tuxguitar.util.properties.TGPropertiesWriter

class TGSharedPreferencesWriter(context: Context, module: String, resource: String) :
    TGSharedPreferencesHandler(context, module, resource),
    TGPropertiesWriter {
    @Throws(TGPropertiesException::class)
    override fun writeProperties(properties: TGProperties, module: String) {
        val map = (properties as TGPropertiesImpl).getMap()
        for ((key, value) in map) {
            putString(key, value)
        }
    }
}
