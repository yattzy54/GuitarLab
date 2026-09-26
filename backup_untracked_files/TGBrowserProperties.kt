package app.tuxguitar.android.browser.config

import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.tools.browser.TGBrowserCollection
import app.tuxguitar.tools.browser.base.TGBrowserSettings
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.properties.TGProperties
import app.tuxguitar.util.properties.TGPropertiesManager
import app.tuxguitar.util.properties.TGPropertiesUtil
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class TGBrowserProperties(private val context: TGContext) {
    private val properties: TGProperties = TGPropertiesManager.getInstance(context).createProperties()

    init {
        load()
    }

    fun load() {
        TGPropertiesManager.getInstance(context).readProperties(properties, RESOURCE, MODULE)
    }

    fun save() {
        TGPropertiesManager.getInstance(context).writeProperties(properties, RESOURCE, MODULE)
    }

    fun getDefaultCollectionIndex(): Int = TGPropertiesUtil.getIntegerValue(properties, PROPERTY_DEFAULT_COLLECTION)

    fun setDefaultCollectionIndex(index: Int) {
        TGPropertiesUtil.setValue(properties, PROPERTY_DEFAULT_COLLECTION, index)
    }

    fun hasCollections(): Boolean {
        val jsonCollections = properties.getValue(PROPERTY_COLLECTIONS)
        return jsonCollections != null && jsonCollections.isNotEmpty()
    }

    @Throws(TGBrowserException::class)
    fun getCollections(): List<TGBrowserCollection> {
        try {
            val collections = ArrayList<TGBrowserCollection>()
            if (hasCollections()) {
                val jsonArray = JSONArray(properties.getValue(PROPERTY_COLLECTIONS))
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val collection = TGBrowserCollection()
                    collection.type = jsonObject.getString(COLLECTION_TYPE)
                    collection.settings = TGBrowserSettings().apply {
                        title = jsonObject.getString(COLLECTION_TITLE)
                        data = jsonObject.getString(COLLECTION_SETTINGS)
                    }
                    collections.add(collection)
                }
            }
            return collections
        } catch (e: JSONException) {
            throw TGBrowserException(e)
        }
    }

    @Throws(TGBrowserException::class)
    fun setCollections(collections: List<TGBrowserCollection>) {
        try {
            val jsonArray = JSONArray()
            for (collection in collections) {
                val jsonObject = JSONObject()
                jsonObject.put(COLLECTION_TYPE, collection.type)
                jsonObject.put(COLLECTION_TITLE, collection.settings.title)
                jsonObject.put(COLLECTION_SETTINGS, collection.settings.data)
                jsonArray.put(jsonObject)
            }
            properties.setValue(PROPERTY_COLLECTIONS, jsonArray.toString())
        } catch (e: JSONException) {
            throw TGBrowserException(e)
        }
    }

    companion object {
        const val MODULE = "tuxguitar"
        const val RESOURCE = "browser"
        const val PROPERTY_COLLECTIONS = "browser-collections"
        const val PROPERTY_DEFAULT_COLLECTION = "default-collection-index"
        const val COLLECTION_TYPE = "type"
        const val COLLECTION_TITLE = "title"
        const val COLLECTION_SETTINGS = "settings"
    }
}
