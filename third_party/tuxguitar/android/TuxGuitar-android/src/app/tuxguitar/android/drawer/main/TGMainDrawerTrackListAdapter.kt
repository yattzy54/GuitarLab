package app.tuxguitar.android.drawer.main

import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import app.tuxguitar.android.R
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.android.view.util.TGProcess
import app.tuxguitar.android.view.util.TGSyncProcessLocked
import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTrack

class TGMainDrawerTrackListAdapter(mainDrawer: TGMainDrawer) : TGMainDrawerListAdapter(mainDrawer) {
    private var selection: TGTrack? = null
    private val eventListener: TGEventListener = TGMainDrawerTrackListListener(this)
    private val items = mutableListOf<TGMainDrawerTrackListItem>()
    private lateinit var updateSelectionProcess: TGProcess
    private lateinit var updateTracksProcess: TGProcess

    init {
        createSyncProcesses()
        processUpdateSelection()
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any? =
        if (position >= 0 && position < items.size) items[position] else null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position) as TGMainDrawerTrackListItem
        val view = convertView
            ?: getLayoutInflater().inflate(R.layout.view_main_drawer_check_item, parent, false)
        view.setOnClickListener(getMainDrawer().getActionHandler().createGoToTrackAction(item.getTrack()!!))
        view.setOnLongClickListener(
            getMainDrawer().getActionHandler().createGoToTrackWithSmartMenuAction(item.getTrack()!!)
        )
        val textView = view.findViewById<CheckedTextView>(R.id.main_drawer_check_item)
        textView.text = item.getLabel()
        textView.isChecked = item.getSelected() == true
        return view
    }

    private fun isUpdateRequired(): Boolean {
        val song = TGDocumentManager.getInstance(getMainDrawer().findContext()).song
        if (song != null) {
            val count = song.countTracks()
            if (count != getCount()) {
                return true
            }
            for (index in 0 until count) {
                val track = song.getTrack(index)
                val item = getItem(index) as? TGMainDrawerTrackListItem
                if (track == null || item == null) {
                    return true
                }
                if (track != item.getTrack()) {
                    return true
                }
                if (track.name != item.getLabel()) {
                    return true
                }
                if (isSelected(track) != (item.getSelected() == true)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isSelected(track: TGTrack?): Boolean = selection != null && selection == track

    private fun updateTrackItems() {
        items.clear()
        val song: TGSong? = TGDocumentManager.getInstance(getMainDrawer().findContext()).song
        if (song != null) {
            val tracks = song.tracks
            while (tracks.hasNext()) {
                val track = tracks.next()
                items.add(
                    TGMainDrawerTrackListItem().apply {
                        setTrack(track)
                        setLabel(track.name)
                        setSelected(isSelected(track))
                    }
                )
            }
        }
    }

    private fun updateSelection() {
        selection = TGSongViewController.getInstance(getMainDrawer().findContext()).caret.track
        if (isUpdateRequired()) {
            updateTracks()
        }
    }

    private fun updateTracks() {
        updateTrackItems()
        notifyDataSetChanged()
    }

    private fun createSyncProcesses() {
        updateSelectionProcess = TGSyncProcessLocked(
            getMainDrawer().findContext(),
            Runnable { updateSelection() }
        )
        updateTracksProcess = TGSyncProcessLocked(
            getMainDrawer().findContext(),
            Runnable { updateTracks() }
        )
    }

    fun processUpdateSelection() {
        updateSelectionProcess.process()
    }

    fun processUpdateTracks() {
        updateTracksProcess.process()
    }

    override fun attachListeners() {
        TGEditorManager.getInstance(getMainDrawer().findContext()).addUpdateListener(eventListener)
    }

    override fun detachListeners() {
        TGEditorManager.getInstance(getMainDrawer().findContext()).removeUpdateListener(eventListener)
    }
}
