package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.util.TGContext
class TGUpdateAddedTrackController : TGUpdateSongController() {
 override fun update(context: TGContext, actionContext: TGActionContext) { val track = actionContext.getAttribute<app.tuxguitar.song.models.TGTrack>(TGDocumentContextAttributes.ATTRIBUTE_TRACK)!!
 findUpdateBuffer(context).doPostUpdate { val c=app.tuxguitar.android.view.tablature.TGSongViewController.getInstance(context).caret; c.update(track.number,c.position,1) }
 super.update(context,actionContext) }
}
