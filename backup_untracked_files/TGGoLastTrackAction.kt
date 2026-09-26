package app.tuxguitar.android.action.impl.track

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.util.TGContext

class TGGoLastTrackAction(context: TGContext) : TGActionBase(context, NAME) {
 override fun processAction(context: TGActionContext) {
  val song = context.getAttribute<TGSong>(TGDocumentContextAttributes.ATTRIBUTE_SONG)
  val track = getSongManager(context).getLastTrack(song)
  if (track != null) getEditor().caret.update(track.number)
 }
 companion object { const val NAME = "action.track.go-last" }
}
