package app.tuxguitar.android.action.impl.track

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTrack
import app.tuxguitar.util.TGContext

class TGGoNextTrackAction(context: TGContext) : TGActionBase(context, NAME) {
 override fun processAction(context: TGActionContext) {
  val song = context.getAttribute<TGSong>(TGDocumentContextAttributes.ATTRIBUTE_SONG)
  val track = context.getAttribute<TGTrack>(TGDocumentContextAttributes.ATTRIBUTE_TRACK)
  val nextTrack = getSongManager(context).getTrack(song, track.number + (1))
  if (nextTrack != null) getEditor().caret.update(nextTrack.number)
 }
 companion object { const val NAME = "action.track.go-next" }
}
