package app.tuxguitar.android.transport

import app.tuxguitar.util.TGContext
import app.tuxguitar.util.properties.TGProperties
import app.tuxguitar.util.properties.TGPropertiesManager
import app.tuxguitar.util.properties.TGPropertiesUtil

class TGTransportProperties(private val context: TGContext) {
    private val properties: TGProperties = TGPropertiesManager.getInstance(context).createProperties()

    init {
        load()
    }

    fun load() {
        TGPropertiesManager.getInstance(context).readProperties(properties, RESOURCE, MODULE)
    }

    fun getMidiOutputPort(): String? = TGPropertiesUtil.getStringValue(properties, PROPERTY_MIDI_OUTPUT_PORT)

    companion object {
        const val MODULE = "tuxguitar"
        const val RESOURCE = "settings"
        const val PROPERTY_MIDI_OUTPUT_PORT = "midi.output.port"
    }
}
