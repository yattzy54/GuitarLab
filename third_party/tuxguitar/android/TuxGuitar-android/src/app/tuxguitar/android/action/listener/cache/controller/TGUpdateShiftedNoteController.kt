package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.util.TGContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.view.tablature.TGSongViewController
class TGUpdateShiftedNoteController : TGUpdateItemsController() { override fun update(context: TGContext, actionContext: TGActionContext) { if(actionContext.getAttribute<Boolean>(app.tuxguitar.action.TGActionEvent.ATTRIBUTE_ACTION_ID)==true) { actionContext.getAttribute<app.tuxguitar.song.models.TGMeasureHeader>(TGDocumentContextAttributes.ATTRIBUTE_HEADER)?.let { findUpdateBuffer(context).requestUpdateMeasure(it.number) }; val s=actionContext.getAttribute<app.tuxguitar.song.models.TGString>(TGDocumentContextAttributes.ATTRIBUTE_STRING)!!; findUpdateBuffer(context).doPostUpdate { val c=TGSongViewController.getInstance(context).caret; c.stringNumber=s.number; c.update() } }; super.update(context,actionContext) } }
