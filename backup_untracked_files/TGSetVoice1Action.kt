package app.tuxguitar.android.action.impl.edit

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.util.TGContext

class TGSetVoice1Action(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        getEditor().caret.setVoice(0)
    }

    companion object {
        const val NAME = "action.edit.voice-1"
    }
}
