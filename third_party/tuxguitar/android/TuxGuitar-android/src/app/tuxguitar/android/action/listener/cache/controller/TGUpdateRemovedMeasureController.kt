package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.util.TGContext
import app.tuxguitar.android.view.tablature.TGSongViewController
class TGUpdateRemovedMeasureController : TGUpdateItemsController() { override fun update(context: TGContext, actionContext: TGActionContext) { if(actionContext.getAttribute<Boolean>(app.tuxguitar.editor.action.measure.TGRemoveMeasureAction.ATTRIBUTE_SUCCESS)==true) { val song=actionContext.getAttribute<app.tuxguitar.song.models.TGSong>(TGDocumentContextAttributes.ATTRIBUTE_SONG)!!; val mgr=actionContext.getAttribute<app.tuxguitar.song.managers.TGSongManager>(app.tuxguitar.song.managers.TGSongManager::class.java.name)!!; findUpdateBuffer(context).requestUpdateSong(); findUpdateBuffer(context).doPostUpdate { val c=TGSongViewController.getInstance(context).caret; val count=song.countMeasureHeaders(); val measure = c.measure; val track = c.track; if (measure != null && track != null && measure.number > count) { val t=mgr.getTrack(song, track.number); val m=mgr.trackManager.getMeasure(t,count); if (m != null) { c.update(track.number,m.start,1) } } } }; super.update(context,actionContext) } }
