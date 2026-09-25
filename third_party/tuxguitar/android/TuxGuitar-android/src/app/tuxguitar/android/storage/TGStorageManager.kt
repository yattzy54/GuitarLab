package app.tuxguitar.android.storage

import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.storage.browser.TGBrowserProvider
import app.tuxguitar.android.storage.saf.TGSafProvider
import app.tuxguitar.util.TGAbstractContext
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGStorageManager private constructor(private val context: TGContext) {
    private var provider: TGStorageProvider? = null

    init {
        createListeners()
    }

    fun createListeners() {
        TGActionManager.getInstance(context).addPostExecutionListener(TGStorageEventListener(context))
    }

    fun openDocument() {
        getProvider()?.openDocument()
    }

    fun saveDocument() {
        getProvider()?.saveDocument()
    }

    fun saveDocumentAs() {
        getProvider()?.saveDocumentAs()
    }

    fun updateSession(source: TGAbstractContext) {
        getProvider()?.updateSession(source)
    }

    fun getProvider(): TGStorageProvider? {
        if (provider == null) {
            loadSettings()
        }
        return provider
    }

    fun loadSettings() {
        var useCollectionBrowser = false
        useCollectionBrowser = TGStorageProperties(context).isUseCollectionBrowser()
        if (!useCollectionBrowser && (provider == null || provider!!::class.java != TGSafProvider::class.java)) {
            provider = TGSafProvider(context)
        } else if (useCollectionBrowser && (provider == null || provider!!::class.java != TGBrowserProvider::class.java)) {
            provider = TGBrowserProvider(context)
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGStorageManager {
            return TGSingletonUtil.getInstance(context, TGStorageManager::class.java.name, object : TGSingletonFactory<TGStorageManager> {
                override fun createInstance(context: TGContext): TGStorageManager = TGStorageManager(context)
            })
        }
    }
}
