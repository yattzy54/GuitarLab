package app.tuxguitar.android.action.impl.caret

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.view.tablature.TGSongViewSmartMenu
import app.tuxguitar.util.TGContext

class TGMoveToAxisPositionAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val x = actionContext.getAttribute<Float>(ATTRIBUTE_X)
        val y = actionContext.getAttribute<Float>(ATTRIBUTE_Y)
        val requestSmartMenu = actionContext.getAttribute<Boolean>(ATTRIBUTE_REQUEST_SMART_MENU) == true
        if (x != null && y != null) {
            getEditor().axisSelector.select(x, y, requestSmartMenu)
        }
    }

    companion object {
        const val NAME = "action.caret.move-to-axis-position"
        const val ATTRIBUTE_X = "positionX"
        const val ATTRIBUTE_Y = "positionY"
        const val ATTRIBUTE_REQUEST_SMART_MENU = TGSongViewSmartMenu.REQUEST_SMART_MENU
    }
}
