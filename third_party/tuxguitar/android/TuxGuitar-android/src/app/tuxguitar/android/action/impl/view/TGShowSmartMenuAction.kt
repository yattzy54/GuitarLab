package app.tuxguitar.android.action.impl.view

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.util.TGContext

class TGShowSmartMenuAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        TGSongViewController.getInstance(getContext()).smartMenu.openSmartMenu(context)
    }

    companion object {
        const val NAME = "action.view.show-smart-menu"
    }
}
