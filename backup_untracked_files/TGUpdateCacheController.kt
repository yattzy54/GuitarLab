package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionAdapterManager
import app.tuxguitar.android.action.listener.cache.TGUpdateBuffer
import app.tuxguitar.android.action.listener.cache.TGUpdateController
import app.tuxguitar.util.TGContext

open class TGUpdateCacheController(
    private val updateItems: Boolean
) : TGUpdateController {
    override fun update(context: TGContext, actionContext: TGActionContext) {
        findUpdateBuffer(context).requestUpdateCache(updateItems)
    }

    fun findUpdateBuffer(context: TGContext): TGUpdateBuffer =
        TGActionAdapterManager.getInstance(context).getUpdatableActionListener().getBuffer()
}
