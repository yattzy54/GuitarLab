package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.util.TGContext
class TGUpdateModifiedNoteController : TGUpdateMeasureController() {
 override fun update(context: TGContext, actionContext: TGActionContext) { if (actionContext.getAttribute<Boolean>(app.tuxguitar.editor.action.note.TGChangeNoteAction.ATTRIBUTE_SUCCESS)==true) app.tuxguitar.android.transport.TGTransportAdapter.getInstance(context).playBeat(actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT)); super.update(context,actionContext) }
}
