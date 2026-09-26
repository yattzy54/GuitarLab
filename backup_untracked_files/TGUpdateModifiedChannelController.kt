package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.util.TGContext
class TGUpdateModifiedChannelController : TGUpdateItemsController() {
 override fun update(context: TGContext, actionContext: TGActionContext) { app.tuxguitar.player.base.MidiPlayer.getInstance(context).updateChannel(actionContext.getAttribute<app.tuxguitar.song.models.TGChannel>(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL)); super.update(context,actionContext) }
}
