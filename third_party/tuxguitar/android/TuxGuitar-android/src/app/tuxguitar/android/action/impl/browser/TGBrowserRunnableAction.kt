package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.util.TGContext

class TGBrowserRunnableAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        actionContext.getAttribute<Runnable>(ATTRIBUTE_RUNNABLE).run()
    }

    companion object {
        const val NAME = "action.util.runnable"
        const val ATTRIBUTE_RUNNABLE = "java.lang.Runnable"
    }
}
