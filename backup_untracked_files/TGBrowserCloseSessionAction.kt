package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionException
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.util.TGContext

class TGBrowserCloseSessionAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        try {
            val manager = TGBrowserManager.getInstance(getContext())
            if (manager.session.browser != null) {
                actionContext.setAttribute(TGBrowserCloseAction.ATTRIBUTE_SESSION, manager.session)
                TGActionManager.getInstance(getContext()).execute(TGBrowserCloseAction.NAME, actionContext)
            }
            manager.closeSession()
        } catch (exception: Exception) {
            throw TGActionException(exception)
        }
    }
    companion object { const val NAME = "action.browser.close-session" }
}
