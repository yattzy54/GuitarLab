package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.util.TGContext

class TGBrowserSaveCurrentElementAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val session = actionContext.getAttribute<TGBrowserSession>(ATTRIBUTE_SESSION)
        val element = session.currentElement
        val format = session.currentFormat
        if (element != null && element.isWritable() && format != null) {
            actionContext.setAttribute(TGBrowserSaveElementAction.ATTRIBUTE_ELEMENT, element)
            actionContext.setAttribute(TGBrowserSaveElementAction.ATTRIBUTE_FORMAT, format)
            TGActionManager.getInstance(getContext()).execute(TGBrowserSaveElementAction.NAME, actionContext)
        } else {
            TGActionManager.getInstance(getContext()).execute(TGBrowserPrepareForWriteAction.NAME, actionContext)
        }
    }
    companion object {
        const val NAME = "action.browser.save-current-element"
        const val ATTRIBUTE_SESSION = TGBrowserSaveElementAction.ATTRIBUTE_SESSION
    }
}
