package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.util.TGContext

open class TGUpdateSongController : TGUpdateItemsController() {
    override fun update(context: TGContext, actionContext: TGActionContext) {
        findUpdateBuffer(context).requestUpdateSong()
        super.update(context, actionContext)
    }
}
