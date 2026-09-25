package app.tuxguitar.android.action.impl.view

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.view.keyboard.TGTabKeyboardController
import app.tuxguitar.util.TGContext

class TGToggleTabKeyboardAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        TGTabKeyboardController.getInstance(getContext()).toggleVisibility()
    }

    companion object {
        const val NAME = "action.view.toogle-tab-keyboard"
    }
}
