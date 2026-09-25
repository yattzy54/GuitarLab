package app.tuxguitar.android.action.impl.caret

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.util.TGContext

class TGGoUpAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        getEditor().caret.moveUp()
    }

    companion object {
        const val NAME = "action.caret.go-up"
    }
}
