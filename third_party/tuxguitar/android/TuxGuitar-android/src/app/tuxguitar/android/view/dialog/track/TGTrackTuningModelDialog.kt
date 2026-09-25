package app.tuxguitar.android.view.dialog.track

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem

class TGTrackTuningModelDialog : TGModalFragment(R.layout.view_track_tuning_model_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.track_tuning_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            if (handleSelection()) close()
            true
        }
    }

    override fun onPostInflateView() {
        fillTuning()
    }

    fun fillPreview() {
        requireView().findViewById<EditText>(R.id.track_tuning_dlg_preview_control)
            .setText(TGTrackTuningLabel.valueOf(findSelectedValue()))
    }

    fun fillTuning() {
        val arrayAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createSelectableTunings()
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinner = requireView().findViewById<Spinner>(R.id.track_tuning_dlg_value_control)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                fillPreview()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }

        val model = getAttribute<TGTrackTuningModel>(TGTrackTuningModelDialogController.ATTRIBUTE_MODEL)
        model?.value?.let { value ->
            spinner.setSelection(arrayAdapter.getPosition(TGSelectableItem(value, null)), false)
        }
    }

    fun findSelectedValue(): Int {
        val selectedItem = requireView()
            .findViewById<Spinner>(R.id.track_tuning_dlg_value_control)
            .selectedItem as TGSelectableItem
        return selectedItem.getItem() as Int
    }

    fun createSelectableTunings(): Array<TGSelectableItem> =
        (0 until MAX_OCTAVES * TGTrackTuningLabel.KEY_NAMES.size).map { value ->
            TGSelectableItem(value, TGTrackTuningLabel.valueOf(value, true))
        }.toTypedArray()

    fun handleSelection(): Boolean {
        val model = TGTrackTuningModel().apply { value = findSelectedValue() }
        val handler = getAttribute<TGTrackTuningModelHandler>(
            TGTrackTuningModelDialogController.ATTRIBUTE_HANDLER
        )
        handler?.handleSelection(model)
        return true
    }

    private companion object {
        const val MAX_OCTAVES = 10
    }
}
