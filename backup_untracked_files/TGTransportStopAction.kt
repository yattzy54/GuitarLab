package app.tuxguitar.android.action.impl.transport

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.transport.TGTransport
import app.tuxguitar.util.TGContext

class TGTransportStopAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        TGTransport.getInstance(getContext()).stop()
    }

    companion object {
        const val NAME = "action.transport.stop"
    }
}
