package app.tuxguitar.android.view.dialog.stroke

import android.annotation.SuppressLint
import android.os.Bundle
import android.R as AndroidR
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.note.TGChangeStrokeAction
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGDuration
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGStroke

class TGStrokeDialog : TGModalFragment(R.layout.view_stroke_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.stroke_dlg_title)
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
        fillDirection()
        fillDurations()
        initializeDurationsState()
    }

    fun createDirectionValues(): Array<TGSelectableItem> = arrayOf(
        TGSelectableItem(TGStroke.STROKE_NONE, getString(R.string.stroke_dlg_direction_none)),
        TGSelectableItem(TGStroke.STROKE_UP, getString(R.string.stroke_dlg_direction_up)),
        TGSelectableItem(TGStroke.STROKE_DOWN, getString(R.string.stroke_dlg_direction_down))
    )

    fun fillDirection() {
        val beat = getBeat()
        val selection = beat?.stroke?.direction ?: TGStroke.STROKE_NONE
        val adapter = ArrayAdapter(
            requireActivity(),
            AndroidR.layout.simple_spinner_item,
            createDirectionValues()
        )
        adapter.setDropDownViewResource(AndroidR.layout.simple_spinner_dropdown_item)
        val spinner = requireView().findViewById<Spinner>(R.id.stroke_dlg_direction_value)
        spinner.adapter = adapter
        spinner.setSelection(adapter.getPosition(TGSelectableItem(selection, null)), false)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateDurationsState(findSelectedDirection() != TGStroke.STROKE_NONE)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                updateDurationsState(findSelectedDirection() != TGStroke.STROKE_NONE)
            }
        }
    }

    fun findSelectedDirection(): Int =
        (requireView().findViewById<Spinner>(R.id.stroke_dlg_direction_value)
            .selectedItem as TGSelectableItem).item as Int

    fun fillDurations() {
        val stroke = getBeat()?.stroke
        val selection = if (stroke != null && stroke.direction != TGStroke.STROKE_NONE) {
            stroke.value
        } else {
            TGDuration.SIXTEENTH
        }
        fillDuration(R.id.stroke_dlg_duration_4, TGDuration.QUARTER, selection)
        fillDuration(R.id.stroke_dlg_duration_8, TGDuration.EIGHTH, selection)
        fillDuration(R.id.stroke_dlg_duration_16, TGDuration.SIXTEENTH, selection)
        fillDuration(R.id.stroke_dlg_duration_32, TGDuration.THIRTY_SECOND, selection)
        fillDuration(R.id.stroke_dlg_duration_64, TGDuration.SIXTY_FOURTH, selection)
    }

    fun fillDuration(id: Int, value: Int, selection: Int) {
        requireView().findViewById<RadioButton>(id).apply {
            tag = value
            isChecked = value == selection
        }
    }

    fun findSelectedDuration(): Int {
        val group = requireView().findViewById<RadioGroup>(R.id.stroke_dlg_duration_group)
        val id = group.checkedRadioButtonId
        return if (id != -1) group.findViewById<RadioButton>(id)?.tag as? Int ?: 0 else 0
    }

    fun initializeDurationsState() {
        updateDurationsState(getBeat()?.stroke?.direction != TGStroke.STROKE_NONE)
    }

    fun updateDurationsState(enabled: Boolean) {
        arrayOf(
            R.id.stroke_dlg_duration_4,
            R.id.stroke_dlg_duration_8,
            R.id.stroke_dlg_duration_16,
            R.id.stroke_dlg_duration_32,
            R.id.stroke_dlg_duration_64
        ).forEach { updateDurationsState(it, enabled) }
    }

    fun updateDurationsState(id: Int, enabled: Boolean) {
        requireView().findViewById<RadioButton>(id).isEnabled = enabled
    }

    fun processAction() {
        val processor = TGActionProcessor(findContext(), TGChangeStrokeAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
        processor.setAttribute(TGChangeStrokeAction.ATTRIBUTE_STROKE_DIRECTION, findSelectedDirection())
        processor.setAttribute(TGChangeStrokeAction.ATTRIBUTE_STROKE_VALUE, findSelectedDuration())
        processor.process()
    }

    fun getMeasure(): TGMeasure? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE)
    fun getBeat(): TGBeat? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT)
}
