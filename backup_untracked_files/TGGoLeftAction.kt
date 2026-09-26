package app.tuxguitar.android.action.impl.caret

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.util.TGContext

class TGGoLeftAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        getEditor().caret.moveLeft()
    }

    companion object {
        const val NAME = "action.caret.go-left"
    }
}
