package app.tuxguitar.android.browser.model

import app.tuxguitar.util.error.TGErrorHandler

interface TGBrowserFactoryHandler : TGErrorHandler {
    fun onCreateBrowser(browser: TGBrowser)
}
