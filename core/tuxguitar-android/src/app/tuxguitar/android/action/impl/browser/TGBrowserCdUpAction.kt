package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.util.TGContext

class TGBrowserCdUpAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val session = actionContext.getAttribute<TGBrowserSession>(ATTRIBUTE_SESSION)
        session.browser?.cdUp(object : TGBrowserActionCallBack<Any>(this@TGBrowserCdUpAction, actionContext) {
            override fun onActionSuccess(actionContext: TGActionContext, successData: Any) = Unit
        })
    }
    companion object {
        const val NAME = "action.browser.cd-up"
        const val ATTRIBUTE_SESSION = "app.tuxguitar.android.browser.model.TGBrowserSession"
    }
}
