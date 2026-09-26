package app.tuxguitar.android.properties

import android.content.Context
import app.tuxguitar.util.properties.TGProperties
import app.tuxguitar.util.properties.TGPropertiesException
import app.tuxguitar.util.properties.TGPropertiesReader

class TGSharedPreferencesReader(
    context: Context,
    module: String,
    resource: String,
    private val defaultReader: TGPropertiesReader?,
) : TGSharedPreferencesHandler(context, module, resource), TGPropertiesReader {
    @Throws(TGPropertiesException::class)
    override fun readProperties(properties: TGProperties, module: String) {
        readDefaultProperties(properties, module)
        readStoredProperties(properties, module)
    }

    private fun readDefaultProperties(properties: TGProperties, module: String) {
        defaultReader?.readProperties(properties, module)
    }

    @Throws(TGPropertiesException::class)
    fun readStoredProperties(properties: TGProperties, module: String) {
        val map = (properties as TGPropertiesImpl).getMap()
        for ((key, value) in getSharedPreferences().all) {
            map[key] = value?.toString()
        }
    }
}
