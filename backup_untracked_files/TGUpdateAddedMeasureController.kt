package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.util.TGContext
import app.tuxguitar.android.view.tablature.TGSongViewController
class TGUpdateAddedMeasureController : TGUpdateItemsController() { override fun update(context: TGContext, actionContext: TGActionContext) { val n=actionContext.getAttribute<Int>(app.tuxguitar.editor.action.measure.TGAddMeasureAction.ATTRIBUTE_MEASURE_NUMBER)!!; val song=actionContext.getAttribute<app.tuxguitar.song.models.TGSong>(TGDocumentContextAttributes.ATTRIBUTE_SONG)!!; val mgr=actionContext.getAttribute<app.tuxguitar.song.managers.TGSongManager>(app.tuxguitar.song.managers.TGSongManager::class.java.name)!!; findUpdateBuffer(context).requestUpdateMeasure(n); findUpdateBuffer(context).doPostUpdate { val c=TGSongViewController.getInstance(context).caret; val track = c.track; if (track != null) { c.update(track.number,mgr.getMeasureHeader(song,n).start,c.stringNumber) } }; super.update(context,actionContext) } }
