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
    val session = TGBrowserSession()
    val properties = TGBrowserProperties(context)
    private val factories = mutableListOf<TGBrowserFactory>()
    private val collections = mutableListOf<TGBrowserCollection>()

    fun getFactories(): Iterator<TGBrowserFactory> = factories.iterator()

    fun getFactory(type: String): TGBrowserFactory? = getFactories().asSequence().firstOrNull { it.type == type }

    fun addFactory(factory: TGBrowserFactory) { factories.add(factory) }
    fun removeFactory(factory: TGBrowserFactory) { factories.remove(factory) }

    fun getCollections(): Iterator<TGBrowserCollection> = collections.iterator()
    fun countCollections(): Int = collections.size
    fun removeCollection(collection: TGBrowserCollection) { collections.remove(collection) }

    fun getCollection(index: Int): TGBrowserCollection? = if (index >= 0 && index < countCollections()) collections[index] else null

    fun getCollection(type: String, settings: TGBrowserSettings): TGBrowserCollection? = collections.firstOrNull {
        it.type == type && it.settings?.title == settings.title && it.settings?.data == settings.data
    }

    fun addCollection(collection: TGBrowserCollection): TGBrowserCollection {
        if (collection.settings != null) {
            val existent = getCollection(collection.type, collection.settings)
            if (existent != null) return existent
            collections.add(collection)
        }
        return collection
    }

    fun createCollection(type: String, settings: TGBrowserSettings): TGBrowserCollection = TGBrowserCollection().apply {
        this.type = type
        this.settings = settings
    }

    @Throws(TGBrowserException::class)
    fun createBrowser(handler: TGBrowserFactoryHandler, collection: TGBrowserCollection) {
        val factory = getFactory(collection.type) ?: return
        factory.createBrowser(handler, collection.settings)
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
        properties.getCollections().forEach { addCollection(it) }
    }

    @Throws(TGBrowserException::class)
    fun storeDefaultCollection() {
        var index = -1
        if (session.collection != null) {
            index = collections.indexOf(session.collection)
        }
        properties.defaultCollectionIndex = index
        properties.save()
    }

    fun getDefaultCollection(): TGBrowserCollection? {
        val count = countCollections()
        if (count > 0) {
            var index = properties.defaultCollectionIndex
            if (index < 0 || index >= count) index = 0
            return getCollection(index)
        }
        return null
    }

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGBrowserManager = TGSingletonUtil.getInstance(
            context,
            TGBrowserManager::class.java.name,
            object : TGSingletonFactory<TGBrowserManager> {
                override fun createInstance(context: TGContext): TGBrowserManager = TGBrowserManager(context)
            }
        )
    }
}
