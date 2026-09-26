package app.tuxguitar.android.action.impl.gui

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuContextualInflater
import app.tuxguitar.android.menu.controller.TGMenuController
import app.tuxguitar.util.TGContext

class TGOpenMenuAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val controller = actionContext.getAttribute<TGMenuController>(ATTRIBUTE_MENU_CONTROLLER)
        TGMenuContextualInflater.getInstance(getContext()).setController(controller)
        val activity = actionContext.getAttribute<TGActivity>(ATTRIBUTE_MENU_ACTIVITY)
        activity.openContextMenu()
    }

    companion object {
        const val NAME = "action.gui.open-menu"
        const val ATTRIBUTE_MENU_ACTIVITY = "app.tuxguitar.android.activity.TGActivity"
        const val ATTRIBUTE_MENU_CONTROLLER = "app.tuxguitar.android.menu.controller.TGMenuController"
    }
}
