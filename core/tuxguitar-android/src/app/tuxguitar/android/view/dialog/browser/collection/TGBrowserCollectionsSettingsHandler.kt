package app.tuxguitar.android.view.dialog.browser.collection

import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.tools.browser.TGBrowserCollection
import app.tuxguitar.tools.browser.base.TGBrowserFactorySettingsHandler
import app.tuxguitar.tools.browser.base.TGBrowserSettings
import app.tuxguitar.util.error.TGErrorManager

class TGBrowserCollectionsSettingsHandler(
    private val dialog: TGBrowserCollectionsDialog,
    private val type: String
) : TGBrowserFactorySettingsHandler {
    override fun onCreateSettings(settings: TGBrowserSettings) {
        dialog.postWhenReady { postOnCreateSettings(settings) }
    }

    override fun handleError(throwable: Throwable) {
        dialog.postWhenReady { postHandleError(throwable) }
    }

    fun postOnCreateSettings(settings: TGBrowserSettings) {
        val collection = TGBrowserManager.getInstance(dialog.findContext()).createCollection(type, settings)
        dialog.addCollection(collection)
    }

    fun postHandleError(throwable: Throwable) {
        TGErrorManager.getInstance(dialog.findContext()).handleError(throwable)
    }
}
