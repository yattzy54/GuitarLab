package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.util.TGContext

class TGBrowserPrepareForWriteAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        actionContext.getAttribute<TGBrowserSession>(ATTRIBUTE_SESSION).sessionType = TGBrowserSession.WRITE_MODE
    }
    companion object {
        const val NAME = "action.browser.prepare-for-write"
        const val ATTRIBUTE_SESSION = "app.tuxguitar.android.browser.model.TGBrowserSession"
    }
}
