package app.tuxguitar.android.action.impl.gui

import android.view.View
import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuCabCallBack
import app.tuxguitar.android.menu.controller.TGMenuController
import app.tuxguitar.util.TGContext

class TGOpenCabMenuAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        actionContext.setAttribute(TGMenuCabCallBack.ATTRIBUTE_BY_PASS_CLOSE_MENU, true)
        val selectableView = actionContext.getAttribute<View>(ATTRIBUTE_MENU_SELECTABLE_VIEW)
        val controller = actionContext.getAttribute<TGMenuController>(ATTRIBUTE_MENU_CONTROLLER)
        val activity = actionContext.getAttribute<TGActivity>(ATTRIBUTE_MENU_ACTIVITY)
        activity.startActionMode(TGMenuCabCallBack(getContext(), controller, selectableView))
    }

    companion object {
        const val NAME = "action.gui.open-cab-menu"
        const val ATTRIBUTE_MENU_ACTIVITY = "app.tuxguitar.android.activity.TGActivity"
        const val ATTRIBUTE_MENU_CONTROLLER = "app.tuxguitar.android.menu.controller.TGMenuController"
        const val ATTRIBUTE_MENU_SELECTABLE_VIEW = "selectableView"
    }
}
