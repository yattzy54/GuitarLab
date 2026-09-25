package app.tuxguitar.android.view.dialog.measure

import android.annotation.SuppressLint
import android.os.Bundle
import android.R as AndroidR
import android.view.Menu
import android.view.MenuInflater
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.measure.TGAddMeasureListAction
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong

class TGMeasureAddDialog : TGModalFragment(R.layout.view_measure_add_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.measure_add_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            processAction()
            close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        fillCount()
        fillOptions()
    }

    fun createCountValues(): Array<TGSelectableItem> =
        Array(100) { index ->
            val count = index + 1
            TGSelectableItem(count, count.toString())
        }

    fun fillCount() {
        val adapter = ArrayAdapter(requireActivity(), AndroidR.layout.simple_spinner_item, createCountValues())
        adapter.setDropDownViewResource(AndroidR.layout.simple_spinner_dropdown_item)
        val spinner = requireView().findViewById<Spinner>(R.id.measure_add_dlg_count_value)
        spinner.adapter = adapter
        spinner.setSelection(adapter.getPosition(TGSelectableItem(1, null)))
    }

    fun findSelectedCount(): Int =
        (requireView().findViewById<Spinner>(R.id.measure_add_dlg_count_value)
            .selectedItem as TGSelectableItem).item as Int

    fun fillOptions() {
        val header = requireNotNull(getHeader())
        fillOption(R.id.measure_add_dlg_options_before_position, header.number, false)
        fillOption(R.id.measure_add_dlg_options_after_position, header.number + 1, false)
        fillOption(R.id.measure_add_dlg_options_at_end, requireNotNull(getSong()).countMeasureHeaders() + 1, true)
    }

    fun fillOption(id: Int, value: Int, selected: Boolean) {
        requireView().findViewById<RadioButton>(id).apply {
            tag = value
            isChecked = selected
        }
    }

    fun findSelectedMeasureNumber(): Int {
        val group = requireView().findViewById<RadioGroup>(R.id.measure_add_dlg_options_group)
        val id = group.checkedRadioButtonId
        return if (id != -1) group.findViewById<RadioButton>(id)?.tag as? Int ?: 1 else 1
    }

    fun processAction() {
        val processor = TGActionProcessor(findContext(), TGAddMeasureListAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_COUNT, findSelectedCount())
        processor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_NUMBER, findSelectedMeasureNumber())
        processor.process()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getHeader(): TGMeasureHeader? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER)
}
