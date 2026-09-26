package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.util.TGContext

class TGBrowserRefreshAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val session = actionContext.getAttribute<TGBrowserSession>(ATTRIBUTE_SESSION)
        session.browser?.listElements(object :
            TGBrowserActionCallBack<List<TGBrowserElement>>(this@TGBrowserRefreshAction, actionContext) {
            override fun onActionSuccess(
                actionContext: TGActionContext,
                successData: List<TGBrowserElement>
            ) {
                session.currentElements = successData
            }
        })
    }
    companion object {
        const val NAME = "action.browser.refresh"
        const val ATTRIBUTE_SESSION = "app.tuxguitar.android.browser.model.TGBrowserSession"
    }
}
