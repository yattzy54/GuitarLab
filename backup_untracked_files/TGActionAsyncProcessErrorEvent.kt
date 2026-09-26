package app.tuxguitar.android.action

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionErrorEvent

class TGActionAsyncProcessErrorEvent(
    actionId: String,
    actionContext: TGActionContext,
    actionError: Throwable,
) : TGActionErrorEvent(EVENT_TYPE, actionId, actionContext, actionError) {
    companion object {
        const val EVENT_TYPE = "action-async-process-error"
    }
}
