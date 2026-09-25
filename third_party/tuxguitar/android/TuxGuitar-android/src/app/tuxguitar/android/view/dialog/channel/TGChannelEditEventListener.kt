package app.tuxguitar.android.view.dialog.channel

import app.tuxguitar.android.view.util.TGProcess
import app.tuxguitar.android.view.util.TGSyncProcessLocked
import app.tuxguitar.editor.event.TGUpdateEvent
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener

class TGChannelEditEventListener(private val handle: TGChannelEditDialog) : TGEventListener {
    private lateinit var updateItems: TGProcess

    init {
        createSyncProcesses()
    }

    fun createSyncProcesses() {
        updateItems = TGSyncProcessLocked(handle.findContext()) {
            if (handle.isReady()) handle.updateItems()
        }
    }

    fun processUpdateEvent(event: TGEvent) {
        val type = event.getAttribute<Int>(TGUpdateEvent.PROPERTY_UPDATE_MODE)!!
        if (type == TGUpdateEvent.SELECTION) updateItems.process()
    }

    override fun processEvent(event: TGEvent) {
        if (TGUpdateEvent.EVENT_TYPE == event.eventType) processUpdateEvent(event)
    }
}
