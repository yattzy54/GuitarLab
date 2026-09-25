package app.tuxguitar.android.action.impl.caret

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.graphics.control.TGMeasureImpl
import app.tuxguitar.graphics.control.TGTrackImpl
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGString
import app.tuxguitar.util.TGContext

class TGMoveToAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val track = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK) as TGTrackImpl
        val measure = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE) as TGMeasureImpl
        val beat = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT) as TGBeat
        val string = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING) as TGString
        getEditor().caret.moveTo(track, measure, beat, string.number)
    }

    companion object {
        const val NAME = "action.caret.move-to"
    }
}
