package app.tuxguitar.android.action.impl.track

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.song.models.TGTrack
import app.tuxguitar.util.TGContext

class TGGoToTrackAction(context: TGContext) : TGActionBase(context, NAME) {
 override fun processAction(context: TGActionContext) {
  context.getAttribute<TGTrack>(TGDocumentContextAttributes.ATTRIBUTE_TRACK)?.let { getEditor().caret.update(it.number) }
 }
 companion object { const val NAME = "action.track.goto" }
}
