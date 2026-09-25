package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.util.TGContext
import app.tuxguitar.android.view.tablature.TGSongViewController
class TGUpdateRemovedTrackController : TGUpdateSongController() { override fun update(context: TGContext, actionContext: TGActionContext) { if(actionContext.getAttribute<Boolean>(app.tuxguitar.editor.action.track.TGRemoveTrackAction.ATTRIBUTE_SUCCESS)==true) { val t=actionContext.getAttribute<app.tuxguitar.song.models.TGTrack>(TGDocumentContextAttributes.ATTRIBUTE_TRACK)!!; findUpdateBuffer(context).doPostUpdate { val c=TGSongViewController.getInstance(context).caret; c.update(t.number,c.measure.start,1) } }; super.update(context,actionContext) } }
