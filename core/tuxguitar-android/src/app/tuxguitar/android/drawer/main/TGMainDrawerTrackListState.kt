package app.tuxguitar.android.drawer.main

import androidx.compose.runtime.mutableStateListOf
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.android.view.util.TGProcess
import app.tuxguitar.android.view.util.TGSyncProcessLocked
import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTrack

class TGMainDrawerTrackListState(private val mainDrawer: TGMainDrawer) {
    private var selection: TGTrack? = null
    private val eventListener: TGEventListener = TGMainDrawerTrackListListener(this)
    val items = mutableStateListOf<TGMainDrawerTrackListItem>()
    private val updateSelectionProcess: TGProcess
    private val updateTracksProcess: TGProcess

    init {
        updateSelectionProcess = TGSyncProcessLocked(mainDrawer.findContext()) { updateSelection() }
        updateTracksProcess = TGSyncProcessLocked(mainDrawer.findContext()) { updateTracks() }
        processUpdateSelection()
    }

    private fun isUpdateRequired(): Boolean {
        val song = TGDocumentManager.getInstance(mainDrawer.findContext()).song
        if (song != null) {
            val count = song.countTracks()
            if (count != items.size) {
                return true
            }
            for (index in 0 until count) {
                val track = song.getTrack(index)
                val item = items.getOrNull(index)
                if (track == null || item == null) {
                    return true
                }
                if (track != item.track) {
                    return true
                }
                if (track.name != item.label) {
                    return true
                }
                if (isSelected(track) != item.selected) {
                    return true
                }
            }
        }
        return false
    }

    private fun isSelected(track: TGTrack?): Boolean = selection != null && selection == track

    private fun updateTrackItems() {
        val updatedItems = mutableListOf<TGMainDrawerTrackListItem>()
        val song: TGSong? = TGDocumentManager.getInstance(mainDrawer.findContext()).song
        if (song != null) {
            val tracks = song.tracks
            while (tracks.hasNext()) {
                val track = tracks.next()
                updatedItems.add(
                    TGMainDrawerTrackListItem(
                        track = track,
                        label = track.name,
                        selected = isSelected(track),
                    ),
                )
            }
        }
        items.clear()
        items.addAll(updatedItems)
    }

    private fun updateSelection() {
        selection = TGSongViewController.getInstance(mainDrawer.findContext()).caret.track
        if (isUpdateRequired()) {
            updateTracks()
        }
    }

    private fun updateTracks() {
        updateTrackItems()
    }

    fun processUpdateSelection() {
        updateSelectionProcess.process()
    }

    fun processUpdateTracks() {
        updateTracksProcess.process()
    }

    fun attachListeners() {
        TGEditorManager.getInstance(mainDrawer.findContext()).addUpdateListener(eventListener)
    }

    fun detachListeners() {
        TGEditorManager.getInstance(mainDrawer.findContext()).removeUpdateListener(eventListener)
    }
}
