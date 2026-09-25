package app.tuxguitar.android.view.dialog.track

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.EditText
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.track.TGSetTrackNameAction
import app.tuxguitar.song.models.TGTrack

class TGTrackNameDialog : TGModalFragment(R.layout.view_track_name_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.track_name_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            updateTrackName()
            close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        setTextFieldValue(R.id.track_name_dlg_name_value, requireNotNull(getTrack()).name)
    }

    fun setTextFieldValue(textFieldId: Int, value: String?) {
        requireView().findViewById<EditText>(textFieldId).text.append(value)
    }

    fun getTextFieldValue(textFieldId: Int): String =
        requireView().findViewById<EditText>(textFieldId).text.toString()

    fun updateTrackName() {
        val processor = TGActionProcessor(findContext(), TGSetTrackNameAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, getTrack())
        processor.setAttribute(
            TGSetTrackNameAction.ATTRIBUTE_TRACK_NAME,
            getTextFieldValue(R.id.track_name_dlg_name_value)
        )
        processor.processOnNewThread()
    }

    fun getTrack(): TGTrack? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK)
}
