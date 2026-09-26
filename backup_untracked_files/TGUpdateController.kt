package app.tuxguitar.android.action.listener.cache

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.util.TGContext

fun interface TGUpdateController {
    fun update(context: TGContext, actionContext: TGActionContext)
}
