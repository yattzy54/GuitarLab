package app.tuxguitar.android.view.dialog.measure

import android.annotation.SuppressLint
import android.os.Bundle
import android.R as AndroidR
import android.view.Menu
import android.view.MenuInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.measure.TGRemoveMeasureRangeAction
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong

class TGMeasureRemoveDialog : TGModalFragment(R.layout.view_measure_remove_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.measure_remove_dlg_title)
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
        fillRanges()
    }

    fun createRangeValues(minimum: Int, maximum: Int): Array<TGSelectableItem> =
        (minimum..maximum).map { TGSelectableItem(it, it.toString()) }.toTypedArray()

    fun fillSpinner(spinner: Spinner, minimum: Int, maximum: Int) {
        val adapter = ArrayAdapter(
            requireActivity(),
            AndroidR.layout.simple_spinner_item,
            createRangeValues(minimum, maximum)
        )
        adapter.setDropDownViewResource(AndroidR.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun fillRanges() {
        val maximum = requireNotNull(getSong()).countMeasureHeaders()
        val selection = requireNotNull(getHeader()).number
        val spinner1 = requireView().findViewById<Spinner>(R.id.measure_remove_dlg_from_value)
        val spinner2 = requireView().findViewById<Spinner>(R.id.measure_remove_dlg_to_value)
        fillSpinner(spinner1, 1, maximum)
        fillSpinner(spinner2, 1, maximum)
        updateSpinnerSelection(spinner1, selection)
        updateSpinnerSelection(spinner2, selection)
        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                validateSpinner1Selection(spinner1, spinner2, 1)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                validateSpinner1Selection(spinner1, spinner2, 1)
            }
        }
        spinner1.onItemSelectedListener = listener
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                validateSpinner2Selection(spinner1, spinner2, maximum)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                validateSpinner2Selection(spinner1, spinner2, maximum)
            }
        }
    }

    fun validateSpinner1Selection(spinner1: Spinner, spinner2: Spinner, minimum: Int) {
        val first = findSelectedValue(spinner1)
        val second = findSelectedValue(spinner2)
        if (first < minimum) updateSpinnerSelection(spinner1, minimum)
        else if (first > second) updateSpinnerSelection(spinner1, second)
    }

    fun validateSpinner2Selection(spinner1: Spinner, spinner2: Spinner, maximum: Int) {
        val first = findSelectedValue(spinner1)
        val second = findSelectedValue(spinner2)
        if (second < first) updateSpinnerSelection(spinner2, first)
        else if (second > maximum) updateSpinnerSelection(spinner2, first)
    }

    fun findSelectedMeasure1(): Int =
        findSelectedValue(requireView().findViewById(R.id.measure_remove_dlg_from_value))

    fun findSelectedMeasure2(): Int =
        findSelectedValue(requireView().findViewById(R.id.measure_remove_dlg_to_value))

    fun findSelectedValue(spinner: Spinner): Int =
        (spinner.selectedItem as TGSelectableItem).item as Int

    fun updateSpinnerSelection(spinner: Spinner, selection: Int) {
        @Suppress("UNCHECKED_CAST")
        val adapter = spinner.adapter as ArrayAdapter<TGSelectableItem>
        spinner.setSelection(adapter.getPosition(TGSelectableItem(selection, null)), false)
    }

    fun processAction() {
        val processor = TGActionProcessor(findContext(), TGRemoveMeasureRangeAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGRemoveMeasureRangeAction.ATTRIBUTE_MEASURE_NUMBER_1, findSelectedMeasure1())
        processor.setAttribute(TGRemoveMeasureRangeAction.ATTRIBUTE_MEASURE_NUMBER_2, findSelectedMeasure2())
        processor.process()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getHeader(): TGMeasureHeader? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER)
}
