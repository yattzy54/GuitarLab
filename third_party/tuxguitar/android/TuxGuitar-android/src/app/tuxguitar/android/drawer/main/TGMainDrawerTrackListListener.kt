package app.tuxguitar.android.drawer.main

import app.tuxguitar.editor.event.TGUpdateEvent
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener

class TGMainDrawerTrackListListener(
    private val adapter: TGMainDrawerTrackListAdapter
) : TGEventListener {
    fun processUpdateEvent(event: TGEvent) {
        when (event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE) as Int) {
            TGUpdateEvent.SELECTION -> adapter.processUpdateSelection()
            TGUpdateEvent.SONG_UPDATED, TGUpdateEvent.SONG_LOADED -> adapter.processUpdateTracks()
        }
    }

    override fun processEvent(event: TGEvent) {
        if (TGUpdateEvent.EVENT_TYPE == event.eventType) {
            processUpdateEvent(event)
        }
    }
}
