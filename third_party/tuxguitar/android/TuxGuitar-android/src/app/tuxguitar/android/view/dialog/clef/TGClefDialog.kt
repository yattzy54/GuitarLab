package app.tuxguitar.android.view.dialog.clef

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.R as AndroidR
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.composition.TGChangeClefAction
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTrack

class TGClefDialog : TGModalFragment(R.layout.view_clef_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.clef_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            changeClef()
            close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        val measure = requireNotNull(getAttribute<TGMeasure>(TGDocumentContextAttributes.ATTRIBUTE_MEASURE))
        val adapter = ArrayAdapter(
            requireActivity(),
            AndroidR.layout.simple_spinner_item,
            createClefValues()
        )
        adapter.setDropDownViewResource(AndroidR.layout.simple_spinner_dropdown_item)
        val spinner = requireView().findViewById<Spinner>(R.id.clef_dlg_clef_value)
        spinner.adapter = adapter
        spinner.setSelection(adapter.getPosition(TGSelectableItem(measure.clef, null)))
        requireView().findViewById<CheckBox>(R.id.clef_dlg_options_apply_to_end).isChecked = true
    }

    fun createClefValues(): Array<TGSelectableItem> = arrayOf(
        TGSelectableItem(TGMeasure.CLEF_TREBLE, getString(R.string.clef_dlg_clef_value_treble)),
        TGSelectableItem(TGMeasure.CLEF_BASS, getString(R.string.clef_dlg_clef_value_bass)),
        TGSelectableItem(TGMeasure.CLEF_TENOR, getString(R.string.clef_dlg_clef_value_tenor)),
        TGSelectableItem(TGMeasure.CLEF_ALTO, getString(R.string.clef_dlg_clef_value_alto))
    )

    fun parseClefValue(): Int =
        requireView().findViewById<Spinner>(R.id.clef_dlg_clef_value)
            .selectedItem.let { (it as TGSelectableItem).item as Int }

    fun parseApplyToEnd(): Boolean =
        requireView().findViewById<CheckBox>(R.id.clef_dlg_options_apply_to_end).isChecked

    fun changeClef() {
        val processor = TGActionProcessor(findContext(), TGChangeClefAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, getTrack())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
        processor.setAttribute(TGChangeClefAction.ATTRIBUTE_CLEF, parseClefValue())
        processor.setAttribute(TGChangeClefAction.ATTRIBUTE_APPLY_TO_END, parseApplyToEnd())
        processor.process()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getTrack(): TGTrack? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK)
    fun getMeasure(): TGMeasure? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE)
}
