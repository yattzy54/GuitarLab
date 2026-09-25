package app.tuxguitar.android.view.dialog.track

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction
import app.tuxguitar.android.fragment.impl.TGChannelListFragmentController
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.track.TGSetTrackChannelAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGChannel
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTrack

class TGTrackChannelDialog : TGModalFragment(R.layout.view_track_channel_dialog) {
    private lateinit var songManager: TGSongManager
    private lateinit var song: TGSong
    private lateinit var track: TGTrack

    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.channel_edit_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_track_channel, menu)
        menu.findItem(R.id.track_channel_dlg_settings_button)
            .setOnMenuItemClickListener(createConfigureInstrumentsAction())
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            updateTrackInstrument()
            close()
            true
        }
    }

    override fun onPostInflateView() {
        songManager = requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER))
        song = requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG))
        track = requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK))
    }

    override fun onShowView() {
        fillTrackInstrument()
    }

    fun createSelectableChannels(): Array<TGSelectableItem> {
        val selectableChannels = mutableListOf(
            TGSelectableItem(null, getString(R.string.global_spinner_select_option))
        )
        songManager.getChannels(song).forEach { channel ->
            selectableChannels.add(TGSelectableItem(channel, channel.getName()))
        }
        return selectableChannels.toTypedArray()
    }

    fun findSelectedChannel(): TGChannel? {
        val selectedItem = requireView()
            .findViewById<Spinner>(R.id.track_channel_dlg_channel_value)
            .selectedItem as? TGSelectableItem
        return selectedItem?.getItem() as? TGChannel
    }

    fun fillTrackInstrument() {
        val arrayAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createSelectableChannels()
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinner = requireView().findViewById<Spinner>(R.id.track_channel_dlg_channel_value)
        spinner.adapter = arrayAdapter
        spinner.setSelection(
            arrayAdapter.getPosition(
                TGSelectableItem(songManager.getChannel(song, track.getChannelId()), null)
            )
        )
    }

    fun createConfigureInstrumentsAction(): TGActionProcessorListener =
        TGActionProcessorListener(findContext(), TGOpenFragmentAction.NAME).apply {
            setAttribute(
                TGOpenFragmentAction.ATTRIBUTE_CONTROLLER,
                TGChannelListFragmentController.getInstance(findContext())
            )
            setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, findActivity())
        }

    fun updateTrackInstrument() {
        TGActionProcessor(findContext(), TGSetTrackChannelAction.NAME).apply {
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track)
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, findSelectedChannel())
            processOnNewThread()
        }
    }
}
