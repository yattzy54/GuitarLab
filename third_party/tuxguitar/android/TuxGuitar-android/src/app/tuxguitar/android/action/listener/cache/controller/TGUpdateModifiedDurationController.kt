package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.util.TGContext
import app.tuxguitar.android.view.tablature.TGSongViewController
class TGUpdateModifiedDurationController : TGUpdateItemsController() { override fun update(context: TGContext, actionContext: TGActionContext) { actionContext.getAttribute<app.tuxguitar.song.models.TGMeasureHeader>(TGDocumentContextAttributes.ATTRIBUTE_HEADER)?.let { findUpdateBuffer(context).requestUpdateMeasure(it.number) }; findUpdateBuffer(context).doPostUpdate { TGSongViewController.getInstance(context).caret.setChanges(true) }; super.update(context,actionContext) } }
