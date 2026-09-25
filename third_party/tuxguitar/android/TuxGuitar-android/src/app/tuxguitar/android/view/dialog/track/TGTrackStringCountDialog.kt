package app.tuxguitar.android.view.dialog.track

import android.annotation.SuppressLint
import android.os.Bundle
import android.R as AndroidR
import android.view.Menu
import android.view.MenuInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.track.TGSetTrackStringCountAction
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTrack

class TGTrackStringCountDialog : TGModalFragment(R.layout.view_track_string_count_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.track_string_count_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            updateStringCount()
            close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        val adapter = ArrayAdapter(
            requireActivity(),
            AndroidR.layout.simple_spinner_item,
            createCountValues()
        )
        adapter.setDropDownViewResource(AndroidR.layout.simple_spinner_dropdown_item)
        val spinner = requireView().findViewById<Spinner>(R.id.track_string_count_dlg_count_value)
        spinner.adapter = adapter
        spinner.setSelection(adapter.getPosition(requireNotNull(getTrack()).stringCount()))
    }

    fun createCountValues(): Array<Int> =
        Array(TGTrack.MAX_STRINGS - TGTrack.MIN_STRINGS + 1) { TGTrack.MIN_STRINGS + it }

    fun parseCount(): Int =
        requireView().findViewById<Spinner>(R.id.track_string_count_dlg_count_value).selectedItem as Int

    fun updateStringCount() {
        val processor = TGActionProcessor(findContext(), TGSetTrackStringCountAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, getTrack())
        processor.setAttribute(TGSetTrackStringCountAction.ATTRIBUTE_STRING_COUNT, parseCount())
        processor.process()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getTrack(): TGTrack? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK)
}
