package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.TGException

class TGUpdateChannelsController : TGUpdateItemsController() {
    override fun update(context: TGContext, actionContext: TGActionContext) {
        try {
            MidiPlayer.getInstance(context).updateChannels()
            super.update(context, actionContext)
        } catch (exception: Exception) {
            throw TGException(exception)
        }
    }
}
