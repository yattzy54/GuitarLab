package app.tuxguitar.android.browser.model

import app.tuxguitar.tools.browser.base.TGBrowserFactorySettingsHandler
import app.tuxguitar.tools.browser.base.TGBrowserSettings

interface TGBrowserFactory {
    fun getName(): String
    fun getType(): String
    fun createSettings(handler: TGBrowserFactorySettingsHandler)
    fun createBrowser(handler: TGBrowserFactoryHandler, settings: TGBrowserSettings)
}
