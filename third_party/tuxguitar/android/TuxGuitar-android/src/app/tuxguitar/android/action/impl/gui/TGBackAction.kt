package app.tuxguitar.android.action.impl.gui

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.util.TGContext

class TGBackAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val activity = actionContext.getAttribute(ATTRIBUTE_ACTIVITY) as TGActivity
        if (!activity.navigationManager.callOpenPreviousFragment()) {
            Thread {
                TGActionManager.getInstance(getContext()).execute(TGExitAction.NAME, actionContext)
            }.start()
        }
    }

    companion object {
        const val NAME = "action.gui.go-back"
        const val ATTRIBUTE_ACTIVITY = "app.tuxguitar.android.activity.TGActivity"
    }
}
