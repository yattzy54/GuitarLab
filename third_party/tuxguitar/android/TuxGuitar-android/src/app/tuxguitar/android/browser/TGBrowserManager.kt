package app.tuxguitar.android.browser

import app.tuxguitar.android.browser.config.TGBrowserProperties
import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.android.browser.model.TGBrowserFactory
import app.tuxguitar.android.browser.model.TGBrowserFactoryHandler
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.tools.browser.TGBrowserCollection
import app.tuxguitar.tools.browser.base.TGBrowserSettings
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGBrowserManager private constructor(private val context: TGContext) {
    val properties: TGBrowserProperties = TGBrowserProperties(context)
    val session: TGBrowserSession = TGBrowserSession()
    private val factories: MutableList<TGBrowserFactory> = ArrayList()
    private val collections: MutableList<TGBrowserCollection> = ArrayList()

    fun getFactories(): java.util.Iterator<TGBrowserFactory> = factories.iterator() as java.util.Iterator<TGBrowserFactory>

    fun getFactory(type: String): TGBrowserFactory? {
        val iterator = getFactories()
        while (iterator.hasNext()) {
            val factory = iterator.next()
            if (factory.getType() == type) {
                return factory
            }
        }
        return null
    }

    fun addFactory(factory: TGBrowserFactory) {
        factories.add(factory)
    }

    fun removeFactory(factory: TGBrowserFactory) {
        factories.remove(factory)
    }

    fun getCollections(): java.util.Iterator<TGBrowserCollection> = collections.iterator() as java.util.Iterator<TGBrowserCollection>

    fun countCollections(): Int = collections.size

    fun removeCollection(collection: TGBrowserCollection) {
        collections.remove(collection)
    }

    fun getCollection(index: Int): TGBrowserCollection? {
        if (index >= 0 && index < countCollections()) {
            return collections[index]
        }
        return null
    }

    fun getCollection(type: String, settings: TGBrowserSettings): TGBrowserCollection? {
        val iterator = getCollections()
        while (iterator.hasNext()) {
            val collection = iterator.next()
            if (collection.type == type && collection.settings?.title == settings.title && collection.settings?.data == settings.data) {
                return collection
            }
        }
        return null
    }

    fun addCollection(collection: TGBrowserCollection): TGBrowserCollection {
        if (collection.settings != null) {
            val existent = getCollection(collection.type, collection.settings)
            if (existent != null) {
                return existent
            }
            collections.add(collection)
        }
        return collection
    }

    fun createCollection(type: String, settings: TGBrowserSettings): TGBrowserCollection {
        val collection = TGBrowserCollection()
        collection.type = type
        collection.settings = settings
        return collection
    }

    @Throws(TGBrowserException::class)
    fun createBrowser(handler: TGBrowserFactoryHandler, collection: TGBrowserCollection) {
        val factory = getFactory(collection.type)
        if (factory != null) {
            factory.createBrowser(handler, collection.settings)
        }
    }

    @Throws(TGBrowserException::class)
    fun closeSession() {
        storeDefaultCollection()
        session.browser = null
        session.collection = null
        session.currentElement = null
        session.currentElements = null
    }

    @Throws(TGBrowserException::class)
    fun openSession(collection: TGBrowserCollection) {
        createBrowser(TGBrowserSessionHandler(context, session, collection), collection)
    }

    @Throws(TGBrowserException::class)
    fun storeCollections() {
        properties.setCollections(collections)
        properties.save()
    }

    fun hasStoredCollections(): Boolean = properties.hasCollections()

    @Throws(TGBrowserException::class)
    fun restoreCollections() {
        collections.clear()
        val storedCollections = properties.getCollections()
        for (collection in storedCollections) {
            addCollection(collection)
        }
    }

    @Throws(TGBrowserException::class)
    fun storeDefaultCollection() {
        var index = -1
        if (session.collection != null) {
            index = collections.indexOf(session.collection)
        }
        properties.setDefaultCollectionIndex(index)
        properties.save()
    }

    fun getDefaultCollection(): TGBrowserCollection? {
        val count = countCollections()
        if (count > 0) {
            var index = properties.getDefaultCollectionIndex()
            if (index < 0 || index >= count) {
                index = 0
            }
            return getCollection(index)
        }
        return null
    }

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGBrowserManager {
            return TGSingletonUtil.getInstance(context, TGBrowserManager::class.java.name, object : TGSingletonFactory<TGBrowserManager> {
                override fun createInstance(context: TGContext): TGBrowserManager = TGBrowserManager(context)
            })
        }
    }
}
