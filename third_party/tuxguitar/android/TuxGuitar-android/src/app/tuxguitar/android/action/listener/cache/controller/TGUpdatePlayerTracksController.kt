package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.util.TGContext

class TGUpdatePlayerTracksController : TGUpdateItemsController() {
    override fun update(context: TGContext, actionContext: TGActionContext) {
        MidiPlayer.getInstance(context).takeIf { it.isRunning }?.updateTracks()
        super.update(context, actionContext)
    }
}
