package app.tuxguitar.android.browser.plugin

import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.android.browser.model.TGBrowserFactory
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.plugin.TGPlugin
import app.tuxguitar.util.plugin.TGPluginException

abstract class TGBrowserPlugin : TGPlugin {
    private var factory: TGBrowserFactory? = null

    @Throws(TGPluginException::class)
    override fun connect(context: TGContext) {
        try {
            if (factory == null) {
                factory = getFactory(context)
                TGBrowserManager.getInstance(context).addFactory(factory!!)
            }
        } catch (throwable: Throwable) {
            throw TGPluginException(throwable.message ?: "", throwable)
        }
    }

    @Throws(TGPluginException::class)
    override fun disconnect(context: TGContext) {
        try {
            if (factory != null) {
                TGBrowserManager.getInstance(context).removeFactory(factory!!)
                factory = null
            }
        } catch (throwable: Throwable) {
            throw TGPluginException(throwable.message ?: "", throwable)
        }
    }

    @Throws(TGPluginException::class)
    protected abstract fun getFactory(context: TGContext): TGBrowserFactory
}
