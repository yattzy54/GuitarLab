package app.tuxguitar.android.action

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionEvent

class TGActionAsyncProcessStartEvent(
    actionId: String,
    actionContext: TGActionContext,
) : TGActionEvent(EVENT_TYPE, actionId, actionContext) {
    companion object {
        const val EVENT_TYPE = "action-async-process-start"
    }
}
