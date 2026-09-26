package app.tuxguitar.android.action.impl.gui

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.util.TGContext

class TGFinishAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        (context.getAttribute(ATTRIBUTE_ACTIVITY) as TGActivity).finish()
    }

    companion object {
        const val NAME = "action.gui.finish"
        const val ATTRIBUTE_ACTIVITY = "app.tuxguitar.android.activity.TGActivity"
    }
}
