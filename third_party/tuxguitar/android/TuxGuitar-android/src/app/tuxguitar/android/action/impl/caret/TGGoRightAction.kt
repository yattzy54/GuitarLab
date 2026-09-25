package app.tuxguitar.android.action.impl.caret

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.view.tablature.TGCaret
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.measure.TGAddMeasureAction
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.util.TGContext

class TGGoRightAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val song = actionContext.getAttribute(
            TGDocumentContextAttributes.ATTRIBUTE_SONG
        ) as TGSong
        val caret: TGCaret = getEditor().caret
        if (!caret.moveRight()) {
            actionContext.setAttribute(
                TGAddMeasureAction.ATTRIBUTE_MEASURE_NUMBER,
                song.countMeasureHeaders() + 1
            )
            TGActionManager.getInstance(getContext()).execute(TGAddMeasureAction.NAME, actionContext)
            caret.moveRight()
        }
    }

    companion object {
        const val NAME = "action.caret.go-right"
    }
}
