package app.tuxguitar.android.action.impl.transport

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.transport.TGTransportAdapter
import app.tuxguitar.util.TGContext

class TGTransportLoadSettingsAction(context: TGContext) : TGActionBase(context, NAME) {
 override fun processAction(context: TGActionContext) { TGTransportAdapter.getInstance(getContext()).loadSettings() }
 companion object { const val NAME = "action.transport.load-settings" }
}
