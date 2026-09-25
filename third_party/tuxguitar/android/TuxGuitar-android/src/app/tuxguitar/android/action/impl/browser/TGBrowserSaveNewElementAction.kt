package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionException
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.util.TGContext

class TGBrowserSaveNewElementAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val name = actionContext.getAttribute<String>(ATTRIBUTE_NAME)
        val session = actionContext.getAttribute<TGBrowserSession>(ATTRIBUTE_SESSION)
        session.browser!!.createElement(object :
            TGBrowserActionCallBack<TGBrowserElement>(this@TGBrowserSaveNewElementAction, actionContext) {
            override fun onActionSuccess(actionContext: TGActionContext, successData: TGBrowserElement) {
                try {
                    if (successData.isWritable()) {
                        actionContext.setAttribute(TGBrowserSaveElementAction.ATTRIBUTE_ELEMENT, successData)
                        TGActionManager.getInstance(getContext()).execute(TGBrowserSaveElementAction.NAME, actionContext)
                    }
                } catch (throwable: Throwable) {
                    throw TGActionException(throwable)
                }
            }
        }, name)
    }
    companion object {
        const val NAME = "action.browser.save-new-element"
        const val ATTRIBUTE_NAME = "elementName"
        const val ATTRIBUTE_SESSION = "app.tuxguitar.android.browser.model.TGBrowserSession"
    }
}
