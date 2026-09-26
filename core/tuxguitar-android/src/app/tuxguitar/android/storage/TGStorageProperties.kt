package app.tuxguitar.android.storage

import app.tuxguitar.util.TGContext
import app.tuxguitar.util.properties.TGProperties
import app.tuxguitar.util.properties.TGPropertiesManager
import app.tuxguitar.util.properties.TGPropertiesUtil

class TGStorageProperties(private val context: TGContext) {
    private val properties: TGProperties =
        TGPropertiesManager.getInstance(context).createProperties()

    init {
        load()
    }

    fun load() {
        TGPropertiesManager.getInstance(context).readProperties(properties, RESOURCE, MODULE)
    }

    fun isUseCollectionBrowser(): Boolean =
        TGPropertiesUtil.getBooleanValue(properties, PROPERTY_COLLECTION_BROWSER)

    companion object {
        const val MODULE = "tuxguitar"
        const val RESOURCE = "settings"
        const val PROPERTY_COLLECTION_BROWSER = "storage.use.collection.browser"
    }
}
