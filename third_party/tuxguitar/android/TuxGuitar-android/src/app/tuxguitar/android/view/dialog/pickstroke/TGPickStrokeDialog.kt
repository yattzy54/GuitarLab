package app.tuxguitar.android.view.dialog.pickstroke

import android.annotation.SuppressLint
import android.os.Bundle
import android.R as AndroidR
import android.view.Menu
import android.view.MenuInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.note.TGChangePickStrokeDownAction
import app.tuxguitar.editor.action.note.TGChangePickStrokeUpAction
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGPickStroke

class TGPickStrokeDialog : TGModalFragment(R.layout.view_pickstroke_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.pickstroke_dlg_title)
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
    }

    fun createDirectionValues(): Array<TGSelectableItem> = arrayOf(
        TGSelectableItem(TGPickStroke.PICK_STROKE_NONE, getString(R.string.pickstroke_dlg_direction_none)),
        TGSelectableItem(TGPickStroke.PICK_STROKE_UP, getString(R.string.pickstroke_dlg_direction_up)),
        TGSelectableItem(TGPickStroke.PICK_STROKE_DOWN, getString(R.string.pickstroke_dlg_direction_down))
    )

    fun fillDirection() {
        val selection = getBeat()?.pickStroke?.direction ?: TGPickStroke.PICK_STROKE_NONE
        val adapter = ArrayAdapter(
            requireActivity(),
            AndroidR.layout.simple_spinner_item,
            createDirectionValues()
        )
        adapter.setDropDownViewResource(AndroidR.layout.simple_spinner_dropdown_item)
        val spinner = requireView().findViewById<Spinner>(R.id.pickstroke_dlg_direction_value)
        spinner.adapter = adapter
        spinner.setSelection(adapter.getPosition(TGSelectableItem(selection, null)), false)
    }

    fun findSelectedDirection(): Int =
        (requireView().findViewById<Spinner>(R.id.pickstroke_dlg_direction_value)
            .selectedItem as TGSelectableItem).item as Int

    fun processAction() {
        val direction = findSelectedDirection()
        if (direction != TGPickStroke.PICK_STROKE_NONE) {
            val action = if (direction == TGPickStroke.PICK_STROKE_UP) {
                TGChangePickStrokeUpAction.NAME
            } else {
                TGChangePickStrokeDownAction.NAME
            }
            TGActionProcessor(findContext(), action).also {
                it.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
                it.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
                it.process()
            }
        }
    }

    fun getMeasure(): TGMeasure? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE)
    fun getBeat(): TGBeat? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT)
}
