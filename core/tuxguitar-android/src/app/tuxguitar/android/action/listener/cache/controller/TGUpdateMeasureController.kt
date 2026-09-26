package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.util.TGContext

open class TGUpdateMeasureController : TGUpdateItemsController() {
    override fun update(context: TGContext, actionContext: TGActionContext) {
        actionContext.getAttribute<TGMeasureHeader>(TGDocumentContextAttributes.ATTRIBUTE_HEADER)
            ?.let { findUpdateBuffer(context).requestUpdateMeasure(it.number) }
        super.update(context, actionContext)
    }
}
