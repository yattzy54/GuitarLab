package app.tuxguitar.android.browser.filesystem

import app.tuxguitar.tools.browser.base.TGBrowserFactorySettingsHandler

interface TGFsBrowserSettingsFactory {
    fun createSettings(handler: TGBrowserFactorySettingsHandler)
}
