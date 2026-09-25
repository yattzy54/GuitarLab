package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.transport.TGTransport
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.util.TGContext

class TGUpdateTransportPositionController : TGUpdateItemsController() {
    override fun update(context: TGContext, actionContext: TGActionContext) {
        if (MidiPlayer.getInstance(context).isRunning) TGTransport.getInstance(context).gotoCaretPosition()
        super.update(context, actionContext)
    }
}
