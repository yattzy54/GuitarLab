package app.tuxguitar.android.action.impl.gui

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.fragment.TGFragmentController
import app.tuxguitar.util.TGContext

class TGOpenFragmentAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val tagId = actionContext.getAttribute<String>(ATTRIBUTE_TAG_ID)
        val controller = actionContext.getAttribute<TGFragmentController<*>>(ATTRIBUTE_CONTROLLER)
        val activity = actionContext.getAttribute<TGActivity>(ATTRIBUTE_ACTIVITY)
        activity.getNavigationManager().processLoadFragment(controller, tagId)
    }

    companion object {
        const val NAME = "action.gui.open-fragment"
        const val ATTRIBUTE_ACTIVITY = "app.tuxguitar.android.activity.TGActivity"
        const val ATTRIBUTE_CONTROLLER = "app.tuxguitar.android.fragment.TGFragmentController"
        const val ATTRIBUTE_TAG_ID = "tagId"
    }
}
