package app.tuxguitar.android.browser.assets

import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.android.browser.model.TGBrowserFactory
import app.tuxguitar.android.browser.model.TGBrowserFactoryHandler
import app.tuxguitar.tools.browser.TGBrowserCollection
import app.tuxguitar.tools.browser.base.TGBrowserFactorySettingsHandler
import app.tuxguitar.tools.browser.base.TGBrowserSettings
import app.tuxguitar.util.TGContext

class TGAssetBrowserFactory(private val context: TGContext) : TGBrowserFactory {
    private val settings: TGAssetBrowserSettings = TGAssetBrowserSettings(context)

    override fun getType(): String = BROWSER_TYPE

    override fun getName(): String = getDefaultSettings().title

    fun getDefaultSettings(): TGAssetBrowserSettings = settings

    override fun createBrowser(handler: TGBrowserFactoryHandler, data: TGBrowserSettings) {
        handler.onCreateBrowser(TGAssetBrowser(context, getDefaultSettings()))
    }

    override fun createSettings(handler: TGBrowserFactorySettingsHandler) {
        handler.onCreateSettings(getDefaultSettings().toBrowserSettings())
    }

    fun createDemoCollection(): TGBrowserCollection =
        TGBrowserManager.getInstance(context).createCollection(getType(), getDefaultSettings().toBrowserSettings())

    companion object {
        const val BROWSER_TYPE = "assets"
    }
}
