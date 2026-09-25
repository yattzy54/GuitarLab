package app.tuxguitar.android.view.dialog.keySignature

import android.annotation.SuppressLint
import android.os.Bundle
import android.R as AndroidR
import android.view.Menu
import android.view.MenuInflater
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.composition.TGChangeKeySignatureAction
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTrack

class TGKeySignatureDialog : TGModalFragment(R.layout.view_key_signature_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.key_signature_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            changeKeySignature()
            close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        val adapter = ArrayAdapter(
            requireActivity(),
            AndroidR.layout.simple_spinner_item,
            createKeyValues()
        )
        adapter.setDropDownViewResource(AndroidR.layout.simple_spinner_dropdown_item)
        val spinner = requireView().findViewById<Spinner>(R.id.key_signature_dlg_ks_value)
        spinner.adapter = adapter
        spinner.setSelection(
            adapter.getPosition(TGSelectableItem(requireNotNull(getMeasure()).keySignature, null))
        )
        requireView().findViewById<CheckBox>(R.id.key_signature_dlg_options_apply_to_end).isChecked = true
    }

    fun createKeyValues(): Array<TGSelectableItem> = arrayOf(
        TGSelectableItem(0, getString(R.string.key_signature_dlg_ks_value_natural)),
        TGSelectableItem(1, getString(R.string.key_signature_dlg_ks_value_sharp_1)),
        TGSelectableItem(2, getString(R.string.key_signature_dlg_ks_value_sharp_2)),
        TGSelectableItem(3, getString(R.string.key_signature_dlg_ks_value_sharp_3)),
        TGSelectableItem(4, getString(R.string.key_signature_dlg_ks_value_sharp_4)),
        TGSelectableItem(5, getString(R.string.key_signature_dlg_ks_value_sharp_5)),
        TGSelectableItem(6, getString(R.string.key_signature_dlg_ks_value_sharp_6)),
        TGSelectableItem(7, getString(R.string.key_signature_dlg_ks_value_sharp_7)),
        TGSelectableItem(8, getString(R.string.key_signature_dlg_ks_value_flat_1)),
        TGSelectableItem(9, getString(R.string.key_signature_dlg_ks_value_flat_2)),
        TGSelectableItem(10, getString(R.string.key_signature_dlg_ks_value_flat_3)),
        TGSelectableItem(11, getString(R.string.key_signature_dlg_ks_value_flat_4)),
        TGSelectableItem(12, getString(R.string.key_signature_dlg_ks_value_flat_5)),
        TGSelectableItem(13, getString(R.string.key_signature_dlg_ks_value_flat_6)),
        TGSelectableItem(14, getString(R.string.key_signature_dlg_ks_value_flat_7))
    )

    fun parseKeySignatureValue(): Int =
        (requireView().findViewById<Spinner>(R.id.key_signature_dlg_ks_value)
            .selectedItem as TGSelectableItem).item as Int

    fun parseApplyToEnd(): Boolean =
        requireView().findViewById<CheckBox>(R.id.key_signature_dlg_options_apply_to_end).isChecked

    fun changeKeySignature() {
        val processor = TGActionProcessor(findContext(), TGChangeKeySignatureAction.NAME)
        processor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_KEY_SIGNATURE, parseKeySignatureValue())
        processor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_END, parseApplyToEnd())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, getTrack())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
        processor.processOnNewThread()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getTrack(): TGTrack? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK)
    fun getMeasure(): TGMeasure? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE)
}
