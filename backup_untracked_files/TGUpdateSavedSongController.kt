package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.util.TGContext

class TGUpdateSavedSongController : TGUpdateItemsController() {
    override fun update(context: TGContext, actionContext: TGActionContext) {
        findUpdateBuffer(context).requestUpdateSavedSong()
        super.update(context, actionContext)
    }
}
