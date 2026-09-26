package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.util.TGContext
import app.tuxguitar.android.view.tablature.TGSongViewController
class TGUpdateModifiedVelocityController : TGUpdateItemsController() { override fun update(context: TGContext, actionContext: TGActionContext) { if(actionContext.getAttribute<Boolean>(app.tuxguitar.editor.action.note.TGChangeVelocityAction.ATTRIBUTE_SUCCESS)==true) { actionContext.getAttribute<app.tuxguitar.song.models.TGMeasureHeader>(TGDocumentContextAttributes.ATTRIBUTE_HEADER)?.let { findUpdateBuffer(context).requestUpdateMeasure(it.number) }; val v=actionContext.getAttribute<Int>(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY)!!; findUpdateBuffer(context).doPostUpdate { TGSongViewController.getInstance(context).caret.velocity=v } }; super.update(context,actionContext) } }
