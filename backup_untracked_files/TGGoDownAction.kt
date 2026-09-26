package app.tuxguitar.android.action.impl.caret

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.util.TGContext

class TGGoDownAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        getEditor().caret.moveDown()
    }

    companion object {
        const val NAME = "action.caret.go-down"
    }
}
