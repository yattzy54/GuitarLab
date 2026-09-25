package app.tuxguitar.android.view.dialog.tripletFeel

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.composition.TGChangeTripletFeelAction
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong

class TGTripletFeelDialog : TGModalFragment(R.layout.view_triplet_feel_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.triplet_feel_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            changeTripletFeel()
            close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        val tripletFeel = getHeader().tripletFeel
        updateRadio(R.id.triplet_feel_dlg_none, TGMeasureHeader.TRIPLET_FEEL_NONE, tripletFeel)
        updateRadio(R.id.triplet_feel_dlg_eighth, TGMeasureHeader.TRIPLET_FEEL_EIGHTH, tripletFeel)
        updateRadio(R.id.triplet_feel_dlg_sixteenth, TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH, tripletFeel)
        requireView().findViewById<CheckBox>(R.id.triplet_feel_dlg_options_apply_to_end).isChecked = true
    }

    fun updateRadio(id: Int, value: Int, selection: Int?) {
        val button = requireView().findViewById<RadioButton>(id)
        button.tag = value
        button.isChecked = selection == value
    }

    fun parseTripletFeelValue(): Int {
        val group = requireView().findViewById<RadioGroup>(R.id.triplet_feel_dlg_value)
        val id = group.checkedRadioButtonId
        return if (id != -1) {
            group.findViewById<RadioButton>(id)?.tag as? Int ?: TGMeasureHeader.TRIPLET_FEEL_NONE
        } else {
            TGMeasureHeader.TRIPLET_FEEL_NONE
        }
    }

    fun parseApplyToEnd(): Boolean =
        requireView().findViewById<CheckBox>(R.id.triplet_feel_dlg_options_apply_to_end).isChecked

    fun changeTripletFeel() {
        val processor = TGActionProcessor(findContext(), TGChangeTripletFeelAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, getHeader())
        processor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_TRIPLET_FEEL, parseTripletFeelValue())
        processor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_APPLY_TO_END, parseApplyToEnd())
        processor.processOnNewThread()
    }

    fun getSong(): TGSong =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG))

    fun getHeader(): TGMeasureHeader =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER))
}
