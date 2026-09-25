package app.tuxguitar.android.drawer.main

import app.tuxguitar.editor.event.TGUpdateEvent
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener

class TGMainDrawerTrackListListener(
    private val trackListState: TGMainDrawerTrackListState,
) : TGEventListener {
    fun processUpdateEvent(event: TGEvent) {
        when (event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE) as Int) {
            TGUpdateEvent.SELECTION -> trackListState.processUpdateSelection()
            TGUpdateEvent.SONG_UPDATED, TGUpdateEvent.SONG_LOADED -> trackListState.processUpdateTracks()
        }
    }

    override fun processEvent(event: TGEvent) {
        if (TGUpdateEvent.EVENT_TYPE == event.eventType) {
            processUpdateEvent(event)
        }
    }
}
