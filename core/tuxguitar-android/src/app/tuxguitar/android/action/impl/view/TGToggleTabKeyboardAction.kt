package app.tuxguitar.android.action.impl.view

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.fragment.impl.TGMainFragmentController
import app.tuxguitar.util.TGContext

class TGToggleTabKeyboardAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        TGMainFragmentController.getInstance(getContext()).getFragment().toggleKeyboard()
    }

    companion object {
        const val NAME = "action.view.toogle-tab-keyboard"
    }
}
