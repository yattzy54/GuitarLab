package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.util.TGContext

class TGBrowserCloseAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val session = actionContext.getAttribute<TGBrowserSession>(ATTRIBUTE_SESSION)
        session.browser?.close(object : TGBrowserActionCallBack<Any>(this@TGBrowserCloseAction, actionContext) {
            override fun onActionSuccess(actionContext: TGActionContext, successData: Any) = Unit
        })
    }

    companion object {
        const val NAME = "action.browser.close"
        const val ATTRIBUTE_SESSION = "app.tuxguitar.android.browser.model.TGBrowserSession"
    }
}
