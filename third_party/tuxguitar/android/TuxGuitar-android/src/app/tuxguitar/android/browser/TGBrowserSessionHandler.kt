package app.tuxguitar.android.browser

import app.tuxguitar.android.action.impl.browser.TGBrowserLoadSessionAction
import app.tuxguitar.android.browser.model.TGBrowser
import app.tuxguitar.android.browser.model.TGBrowserFactoryHandler
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.tools.browser.TGBrowserCollection
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.error.TGErrorManager

class TGBrowserSessionHandler(
    private val context: TGContext,
    private val session: TGBrowserSession,
    private val collection: TGBrowserCollection
) : TGBrowserFactoryHandler {
    override fun onCreateBrowser(browser: TGBrowser) {
        val tgActionProcessor = TGActionProcessor(context, TGBrowserLoadSessionAction.NAME)
        tgActionProcessor.setAttribute(TGBrowserLoadSessionAction.ATTRIBUTE_SESSION, session)
        tgActionProcessor.setAttribute(TGBrowserLoadSessionAction.ATTRIBUTE_COLLECTION, collection)
        tgActionProcessor.setAttribute(TGBrowserLoadSessionAction.ATTRIBUTE_BROWSER, browser)
        tgActionProcessor.process()
    }

    override fun handleError(throwable: Throwable) {
        TGErrorManager.getInstance(context).handleError(throwable)
    }
}
