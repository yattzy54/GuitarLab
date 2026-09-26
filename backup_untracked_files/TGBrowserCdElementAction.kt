package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.util.TGContext

class TGBrowserCdElementAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val session = actionContext.getAttribute<TGBrowserSession>(ATTRIBUTE_SESSION)
        val element = actionContext.getAttribute<TGBrowserElement>(ATTRIBUTE_ELEMENT)
        session.browser?.cdElement(
            object : TGBrowserActionCallBack<Any>(this@TGBrowserCdElementAction, actionContext) {
                override fun onActionSuccess(actionContext: TGActionContext, successData: Any) = Unit
            },
            element
        )
    }
    companion object {
        const val NAME = "action.browser.cd-element"
        const val ATTRIBUTE_SESSION = "app.tuxguitar.android.browser.model.TGBrowserSession"
        const val ATTRIBUTE_ELEMENT = "app.tuxguitar.android.browser.model.TGBrowserElement"
    }
}
