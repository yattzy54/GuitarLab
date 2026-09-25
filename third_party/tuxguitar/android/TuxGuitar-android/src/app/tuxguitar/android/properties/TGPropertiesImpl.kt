package app.tuxguitar.android.properties

import app.tuxguitar.util.properties.TGProperties
import java.util.Properties

class TGPropertiesImpl : TGProperties {
    private val properties = HashMap<String, String?>()

    fun getMap(): MutableMap<String, String?> = properties

    override fun getValue(key: String): String? = properties[key]

    override fun setValue(key: String, value: String?) {
        properties[key] = value
    }

    override fun remove(key: String) {
        properties.remove(key)
    }

    override fun clear() {
        properties.clear()
    }

    override fun update(newProperties: Properties) {
        val newPropertiesMap = HashMap<String, String?>()
        for ((key, value) in newProperties.entries) {
            newPropertiesMap[key.toString()] = value?.toString()
        }
        properties.putAll(newPropertiesMap)
    }

    override fun getStringKeys(): MutableSet<String> = properties.keys
}
