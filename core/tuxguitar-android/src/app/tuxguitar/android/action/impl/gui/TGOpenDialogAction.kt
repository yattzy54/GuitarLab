package app.tuxguitar.android.action.impl.gui

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.view.dialog.TGDialogContext
import app.tuxguitar.android.view.dialog.TGDialogController
import app.tuxguitar.util.TGContext

class TGOpenDialogAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val activity = actionContext.getAttribute<TGActivity>(ATTRIBUTE_DIALOG_ACTIVITY)
        val controller = actionContext.getAttribute<TGDialogController>(ATTRIBUTE_DIALOG_CONTROLLER)
        controller.showDialog(activity, createDialogContext(actionContext))
    }

    private fun createDialogContext(context: TGActionContext): TGDialogContext =
        TGDialogContext().also { dialogContext ->
            context.attributes.forEach { (key, value) -> dialogContext.setAttribute(key, value) }
        }

    companion object {
        const val NAME = "action.gui.open-dialog"
        const val ATTRIBUTE_DIALOG_ACTIVITY = "app.tuxguitar.android.activity.TGActivity"
        const val ATTRIBUTE_DIALOG_CONTROLLER = "app.tuxguitar.android.view.dialog.TGDialogController"
    }
}
