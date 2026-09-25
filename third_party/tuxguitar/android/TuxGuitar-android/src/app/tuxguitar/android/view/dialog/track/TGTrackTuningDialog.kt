package app.tuxguitar.android.view.dialog.track

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ListView
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.dialog.message.TGMessageDialogController
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.track.TGChangeTrackTuningAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGString
import app.tuxguitar.song.models.TGTrack
import app.tuxguitar.song.models.TGTuning

class TGTrackTuningDialog : TGModalFragment(R.layout.view_track_tuning_dialog) {
    val tuning = mutableListOf<TGTrackTuningModel>()
    private val tuningPresets = mutableListOf<TGTrackTuningPresetModel>()
    val actionHandler = TGTrackTuningActionHandler(this)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.track_tuning_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_track_tuning, menu)
        menu.findItem(R.id.action_add)
            .setOnMenuItemClickListener(actionHandler.createAddTuningModelAction())
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            if (updateTrackProperties()) close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        val songManager = requireNotNull(
            getAttribute<TGSongManager>(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER)
        )
        val song = requireNotNull(getAttribute<TGSong>(TGDocumentContextAttributes.ATTRIBUTE_SONG))
        val track = requireNotNull(getAttribute<TGTrack>(TGDocumentContextAttributes.ATTRIBUTE_TRACK))
        val percussionChannel = songManager.isPercussionChannel(song, track.getChannelId())

        createTuningPresets()
        fillTuningListView()
        fillOffset(track)
        fillPreset()
        updateTuningFromTrack(track)
        updateItems(percussionChannel)
    }

    fun createSelectableIntegers(minimum: Int, maximum: Int): Array<TGSelectableItem> =
        (minimum..maximum).map { value ->
            TGSelectableItem(
                value,
                findActivity().getString(R.string.track_tuning_dlg_offset_select_value, value)
            )
        }.toTypedArray()

    fun createSelectableOffsets(): Array<TGSelectableItem> =
        createSelectableIntegers(TGTrack.MIN_OFFSET, TGTrack.MAX_OFFSET)

    fun createSelectablePresets(): Array<TGSelectableItem> =
        (listOf(TGSelectableItem(null, findActivity().getString(
            R.string.track_tuning_dlg_preset_select_value
        ))).plus(tuningPresets.map { TGSelectableItem(it, createTuningPresetLabel(it)) }))
            .toTypedArray()

    fun findSelectedOffset(): Int =
        (requireView().findViewById<Spinner>(R.id.track_tuning_dlg_offset_value)
            .selectedItem as TGSelectableItem).getItem() as Int

    fun findSelectedPreset(): TGTrackTuningPresetModel? =
        (requireView().findViewById<Spinner>(R.id.track_tuning_dlg_preset_value)
            .selectedItem as? TGSelectableItem)?.getItem() as? TGTrackTuningPresetModel

    fun findOptionValue(optionId: Int): Boolean =
        requireView().findViewById<CheckBox>(optionId).isChecked

    fun findSelectedTuning(): List<TGString> {
        val songManager = requireNotNull(
            getAttribute<TGSongManager>(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER)
        )
        return tuning.mapIndexed { index, model ->
            TGSongManager.newString(
                songManager.getFactory(),
                index + 1,
                requireNotNull(model.value) { "Tuning model has no value" }
            )
        }
    }

    fun fillTuningListView() {
        requireView().findViewById<ListView>(R.id.track_tuning_dlg_list_view).apply {
            adapter = TGTrackTuningAdapter(this@TGTrackTuningDialog, requireView().context)
        }
        updateTuningListView()
    }

    fun fillOffset(track: TGTrack) {
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createSelectableOffsets()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        requireView().findViewById<Spinner>(R.id.track_tuning_dlg_offset_value).apply {
            this.adapter = adapter
            setSelection(adapter.getPosition(TGSelectableItem(track.getOffset(), null)), false)
        }
    }

    fun fillPreset() {
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createSelectablePresets()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        requireView().findViewById<Spinner>(R.id.track_tuning_dlg_preset_value).apply {
            this.adapter = adapter
            onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    onSelectPreset()
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
            }
        }
    }

    fun updateItems(percussionChannel: Boolean) {
        updateOffset(!percussionChannel)
    }

    fun updateOffset(enabled: Boolean) {
        requireView().findViewById<Spinner>(R.id.track_tuning_dlg_offset_value).isEnabled = enabled
    }

    private fun updateTuningPresetSelection() {
        val selection = tuningPresets.lastOrNull { isUsingPreset(it) }
        if (selection != findSelectedPreset()) {
            val spinner = requireView().findViewById<Spinner>(R.id.track_tuning_dlg_preset_value)
            @Suppress("UNCHECKED_CAST")
            val adapter = spinner.adapter as ArrayAdapter<TGSelectableItem>
            spinner.setSelection(adapter.getPosition(TGSelectableItem(selection, null)), false)
        }
    }

    private fun isUsingPreset(preset: TGTrackTuningPresetModel): Boolean {
        val values = preset.values ?: return false
        return tuning.size == values.size && tuning.indices.all { index ->
            tuning[index].value == values[index].value
        }
    }

    fun updateTuningControls() {
        updateTuningListView()
        updateTuningPresetSelection()
    }

    fun updateTuningListView() {
        (requireView().findViewById<ListView>(R.id.track_tuning_dlg_list_view)
            .adapter as TGTrackTuningAdapter).notifyDataSetChanged()
    }

    fun updateTuningFromTrack(track: TGTrack) {
        tuning.clear()
        for (index in 0 until track.stringCount()) {
            val string = track.getString(index + 1)
            tuning.add(TGTrackTuningModel().apply { value = string.getValue() })
        }
        updateTuningControls()
    }

    private fun updateTuningFromPreset(preset: TGTrackTuningPresetModel) {
        val models = preset.values.orEmpty().map { presetModel ->
            TGTrackTuningModel().apply { value = presetModel.value }
        }
        updateTuningModels(models)
    }

    fun modifyTuningModel(model: TGTrackTuningModel, value: Int?) {
        if (tuning.contains(model)) {
            model.value = value
            updateTuningControls()
        }
    }

    fun addTuningModel(model: TGTrackTuningModel) {
        if (tuning.add(model)) updateTuningControls()
    }

    fun removeTuningModel(model: TGTrackTuningModel) {
        if (tuning.remove(model)) updateTuningControls()
    }

    fun updateTuningModels(models: List<TGTrackTuningModel>) {
        tuning.clear()
        if (tuning.addAll(models)) updateTuningControls()
    }

    private fun onSelectPreset() {
        findSelectedPreset()?.let(::updateTuningFromPreset)
    }

    fun createTuningPreset(tuning: TGTuning): TGTrackTuningPresetModel {
        val models = tuning.getValues().map { value ->
            TGTrackTuningModel().apply { this.value = value }
        }.toTypedArray()
        return TGTrackTuningPresetModel().apply {
            name = tuning.getName()
            values = models
        }
    }

    fun createTuningPresets() {
        tuningPresets.clear()
        tuningPresets.addAll(findActivity().getTuningManager().getTgTunings().map(::createTuningPreset))
    }

    fun createTuningPresetLabel(tuningPreset: TGTrackTuningPresetModel): String =
        tuningPreset.name.orEmpty()

    private fun hasTuningChanges(newStrings: List<TGString>): Boolean {
        val track = getAttribute<TGTrack>(TGDocumentContextAttributes.ATTRIBUTE_TRACK)
            ?: return false
        val oldStrings = track.getStrings()
        if (oldStrings.size != newStrings.size) return true
        return oldStrings.any { oldString ->
            newStrings.none { newString -> newString.isEqual(oldString) }
        }
    }

    private fun hasOffsetChanges(offset: Int?): Boolean {
        val track = getAttribute<TGTrack>(TGDocumentContextAttributes.ATTRIBUTE_TRACK)
            ?: return false
        return offset != null && offset != track.getOffset()
    }

    fun updateTrackProperties(): Boolean {
        val track = getAttribute<TGTrack>(TGDocumentContextAttributes.ATTRIBUTE_TRACK)
        val processor = TGActionProcessor(findContext(), TGChangeTrackTuningAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track)

        val strings = findSelectedTuning()
        if (!validateTrackTuning(strings)) return false
        if (hasTuningChanges(strings)) {
            processor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_STRINGS, strings)
        }
        val offset = findSelectedOffset()
        if (hasOffsetChanges(offset)) {
            processor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_OFFSET, offset)
        }
        processor.process()
        return true
    }

    private fun validateTrackTuning(strings: List<TGString>): Boolean {
        if (strings.size < TGTrack.MIN_STRINGS || strings.size > TGTrack.MAX_STRINGS) {
            showErrorMessage(
                findActivity().getString(
                    R.string.track_tuning_dlg_range_error,
                    TGTrack.MIN_STRINGS,
                    TGTrack.MAX_STRINGS
                )
            )
            return false
        }
        return true
    }

    fun showErrorMessage(message: String) {
        TGActionProcessor(findContext(), TGOpenDialogAction.NAME).apply {
            setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, findActivity())
            setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, TGMessageDialogController())
            setAttribute(
                TGMessageDialogController.ATTRIBUTE_TITLE,
                findActivity().getString(R.string.track_tuning_dlg_error_title)
            )
            setAttribute(TGMessageDialogController.ATTRIBUTE_MESSAGE, message)
            process()
        }
    }

    fun postModifyTuningModel(model: TGTrackTuningModel, value: Int?) {
        postWhenReady { modifyTuningModel(model, value) }
    }

    fun postAddTuningModel(model: TGTrackTuningModel) {
        postWhenReady { addTuningModel(model) }
    }

    fun postRemoveTuningModel(model: TGTrackTuningModel) {
        postWhenReady { removeTuningModel(model) }
    }
}
