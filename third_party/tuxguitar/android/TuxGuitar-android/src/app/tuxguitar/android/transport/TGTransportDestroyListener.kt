package app.tuxguitar.android.transport

import app.tuxguitar.editor.event.TGDestroyEvent
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener

class TGTransportDestroyListener(private val adapter: TGTransportAdapter) : TGEventListener {
    override fun processEvent(event: TGEvent) {
        if (TGDestroyEvent.EVENT_TYPE == event.eventType) {
            adapter.destroy()
        }
    }
}
