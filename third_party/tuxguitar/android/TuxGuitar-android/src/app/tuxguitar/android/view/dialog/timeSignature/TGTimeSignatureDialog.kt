package app.tuxguitar.android.view.dialog.timeSignature

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
import app.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTimeSignature

class TGTimeSignatureDialog : TGModalFragment(R.layout.view_time_signature_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.time_signature_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            changeTimeSignature()
            close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        val header = requireNotNull(getHeader())
        val numeratorAdapter = createAdapter(createNumeratorValues())
        val numerator = requireView().findViewById<Spinner>(R.id.time_signature_dlg_ts_numerator_value)
        numerator.adapter = numeratorAdapter
        numerator.setSelection(
            numeratorAdapter.getPosition(TGSelectableItem(header.timeSignature.numerator, null))
        )
        val denominatorAdapter = createAdapter(createDenominatorValues())
        val denominator = requireView().findViewById<Spinner>(R.id.time_signature_dlg_ts_denominator_value)
        denominator.adapter = denominatorAdapter
        denominator.setSelection(
            denominatorAdapter.getPosition(
                TGSelectableItem(header.timeSignature.denominator.value, null)
            )
        )
        requireView().findViewById<CheckBox>(R.id.time_signature_dlg_options_apply_to_end).isChecked = true
    }

    private fun createAdapter(values: Array<TGSelectableItem>) =
        ArrayAdapter(requireActivity(), AndroidR.layout.simple_spinner_item, values).apply {
            setDropDownViewResource(AndroidR.layout.simple_spinner_dropdown_item)
        }

    fun createNumeratorValues(): Array<TGSelectableItem> =
        Array(32) { index ->
            val value = index + 1
            TGSelectableItem(value, value.toString())
        }

    fun createDenominatorValues(): Array<TGSelectableItem> {
        val values = mutableListOf<TGSelectableItem>()
        var value = 1
        while (value <= 32) {
            values.add(TGSelectableItem(value, value.toString()))
            value *= 2
        }
        return values.toTypedArray()
    }

    fun parseTimeSignature(): TGTimeSignature {
        val numerator = parseSelectedValue(R.id.time_signature_dlg_ts_numerator_value)
        val denominator = parseSelectedValue(R.id.time_signature_dlg_ts_denominator_value)
        return getSongManager().factory.newTimeSignature().also {
            it.setNumerator(numerator)
            it.denominator.setValue(denominator)
        }
    }

    private fun parseSelectedValue(id: Int): Int =
        (requireView().findViewById<Spinner>(id).selectedItem as TGSelectableItem).item as Int

    fun parseNumeratorValue(spinner: Spinner): Int =
        (spinner.selectedItem as TGSelectableItem).item as Int

    fun parseDenominatorValue(spinner: Spinner): Int =
        (spinner.selectedItem as TGSelectableItem).item as Int

    fun parseApplyToEnd(): Boolean =
        requireView().findViewById<CheckBox>(R.id.time_signature_dlg_options_apply_to_end).isChecked

    fun changeTimeSignature() {
        val processor = TGActionProcessor(findContext(), TGChangeTimeSignatureAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, getHeader())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE, parseTimeSignature())
        processor.setAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_APPLY_TO_END, parseApplyToEnd())
        processor.processOnNewThread()
    }

    fun getSongManager(): TGSongManager =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER))
    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getHeader(): TGMeasureHeader? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER)
}
