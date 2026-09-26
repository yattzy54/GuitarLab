package app.tuxguitar.android.view.dialog.track

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.tuxguitar.android.ui.state.editorViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.android.view.dialog.compose.TGDialogActionButtons
import app.tuxguitar.android.view.dialog.compose.TGDialogDropdownField
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

class TGTrackTuningDialog : TGComposeDialog() {
    val tuning: List<TGTrackTuningModel> get() = viewModel.state.value.tuning
    private val tuningPresets get() = viewModel.state.value.presets
    val actionHandler = TGTrackTuningActionHandler(this)
    private val viewModel by lazy {
        editorViewModel {
            val songManager = requireNotNull(getAttribute<TGSongManager>(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER))
            val song = requireNotNull(getAttribute<TGSong>(TGDocumentContextAttributes.ATTRIBUTE_SONG))
            val track = requireNotNull(getAttribute<TGTrack>(TGDocumentContextAttributes.ATTRIBUTE_TRACK))
            TGTrackTuningDialogViewModel(
                TGTrackTuningState(
                    tuning = (0 until track.stringCount()).map { index ->
                        TGTrackTuningModel().apply { value = track.getString(index + 1).getValue() }
                    },
                    presets = findActivity().getTuningManager().getTgTunings().map(::createTuningPreset),
                    selectedOffset = track.getOffset(),
                    offsetEnabled = !songManager.isPercussionChannel(song, track.getChannelId()),
                )
            )
        }
    }

    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val state by viewModel.state.collectAsStateWithLifecycle()

        val offsetOptions = createSelectableOffsets().toList()
        val presetOptions = createSelectablePresets().toList()
        val selectedOffsetIndex = offsetOptions.indexOfFirst {
            (it.getItem() as? Int) == state.selectedOffset
        }.takeIf { it >= 0 } ?: 0
        val selectedPresetIndex = presetOptions.indexOfFirst {
            (it.getItem() as? TGTrackTuningPresetModel) == state.selectedPreset
        }.takeIf { it >= 0 } ?: 0

        TGTrackTuningDialogContent(
            title = stringResource(R.string.track_tuning_dlg_title),
            addLabel = stringResource(R.string.global_button_add),
            offsetLabel = stringResource(R.string.track_tuning_dlg_offset_label),
            presetLabel = stringResource(R.string.track_tuning_dlg_preset_label),
            offsetOptions = offsetOptions.map { it.getLabel().orEmpty() },
            selectedOffsetIndex = selectedOffsetIndex,
            offsetEnabled = state.offsetEnabled,
            presetOptions = presetOptions.map { it.getLabel().orEmpty() },
            selectedPresetIndex = selectedPresetIndex,
            tuningLabels = state.tuning.map { it.getName() },
            editLabel = stringResource(R.string.action_track_tuning_list_item_edit),
            removeLabel = stringResource(R.string.action_track_tuning_list_item_remove),
            onAdd = actionHandler::openAddTuningModelDialog,
            onSelectOffset = { index ->
                viewModel.selectOffset(offsetOptions[index].getItem() as Int)
            },
            onSelectPreset = { index ->
                viewModel.selectPreset(presetOptions[index].getItem() as? TGTrackTuningPresetModel)
            },
            onEditTuning = { index ->
                tuning.getOrNull(index)?.let(actionHandler::openEditTuningModelDialog)
            },
            onRemoveTuning = { index ->
                tuning.getOrNull(index)?.let(actionHandler::removeTuningModel)
            },
            onConfirm = {
                if (updateTrackProperties()) {
                    onDismiss()
                }
            },
            onCancel = onDismiss,
        )
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
        (listOf(
            TGSelectableItem(
                null,
                findActivity().getString(R.string.track_tuning_dlg_preset_select_value)
            )
        ) + tuningPresets.map { TGSelectableItem(it, createTuningPresetLabel(it)) }).toTypedArray()

    fun findSelectedOffset(): Int = viewModel.state.value.selectedOffset

    fun findSelectedPreset(): TGTrackTuningPresetModel? = viewModel.state.value.selectedPreset

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

    fun updateItems(percussionChannel: Boolean) {
        updateOffset(!percussionChannel)
    }

    fun updateOffset(enabled: Boolean) {
        viewModel.setOffsetEnabled(enabled)
    }

    fun updateTuningFromTrack(track: TGTrack) {
        val models = (0 until track.stringCount()).map { index ->
            TGTrackTuningModel().apply { value = track.getString(index + 1).getValue() }
        }
        updateTuningModels(models)
    }

    fun modifyTuningModel(model: TGTrackTuningModel, value: Int?) {
        viewModel.modify(model, value)
    }

    fun addTuningModel(model: TGTrackTuningModel) {
        viewModel.add(model)
    }

    fun removeTuningModel(model: TGTrackTuningModel) {
        viewModel.remove(model)
    }

    fun updateTuningModels(models: List<TGTrackTuningModel>) {
        viewModel.setTuning(models)
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
        if (!viewModel.isValidTuning) {
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
        postWhenReady(Runnable { modifyTuningModel(model, value) })
    }

    fun postAddTuningModel(model: TGTrackTuningModel) {
        postWhenReady(Runnable { addTuningModel(model) })
    }

    fun postRemoveTuningModel(model: TGTrackTuningModel) {
        postWhenReady(Runnable { removeTuningModel(model) })
    }
}

@Composable
fun TGTrackTuningDialogContent(
    title: String,
    addLabel: String,
    offsetLabel: String,
    presetLabel: String,
    offsetOptions: List<String>,
    selectedOffsetIndex: Int,
    offsetEnabled: Boolean,
    presetOptions: List<String>,
    selectedPresetIndex: Int,
    tuningLabels: List<String>,
    editLabel: String,
    removeLabel: String,
    onAdd: () -> Unit,
    onSelectOffset: (Int) -> Unit,
    onSelectPreset: (Int) -> Unit,
    onEditTuning: (Int) -> Unit,
    onRemoveTuning: (Int) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp),
            )
            IconButton(onClick = onAdd) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = addLabel,
                )
            }
        }
        TGDialogDropdownField(
            label = presetLabel,
            selectedText = presetOptions[selectedPresetIndex],
            options = presetOptions,
            onOptionSelected = onSelectPreset,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
        TGDialogDropdownField(
            label = offsetLabel,
            selectedText = offsetOptions[selectedOffsetIndex],
            options = offsetOptions,
            onOptionSelected = onSelectOffset,
            enabled = offsetEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .padding(top = 16.dp),
        ) {
            itemsIndexed(tuningLabels, key = { index, _ -> index }) { index, label ->
                TGTrackTuningListItem(
                    label = label,
                    editLabel = editLabel,
                    removeLabel = removeLabel,
                    onEdit = { onEditTuning(index) },
                    onRemove = { onRemoveTuning(index) },
                )
                if (index < tuningLabels.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
        TGDialogActionButtons(
            onConfirm = onConfirm,
            onCancel = onCancel,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TGTrackTuningListItem(
    label: String,
    editLabel: String,
    removeLabel: String,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
) {
    var expanded by androidx.compose.runtime.remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = {}, onLongClick = { expanded = true }),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(editLabel) },
                onClick = {
                    expanded = false
                    onEdit()
                },
            )
            DropdownMenuItem(
                text = { Text(removeLabel) },
                onClick = {
                    expanded = false
                    onRemove()
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGTrackTuningDialogContentPreview() {
    GuitarLabTheme {
        TGTrackTuningDialogContent(
            title = "Tuning",
            addLabel = "Add",
            offsetLabel = "Offset",
            presetLabel = "Preset",
            offsetOptions = listOf("Offset #0", "Offset #1", "Offset #2"),
            selectedOffsetIndex = 0,
            offsetEnabled = true,
            presetOptions = listOf("-- Presets --", "Standard", "Drop D"),
            selectedPresetIndex = 1,
            tuningLabels = listOf("E", "A", "D", "G", "B", "E"),
            editLabel = "Edit",
            removeLabel = "Remove",
            onAdd = {},
            onSelectOffset = {},
            onSelectPreset = {},
            onEditTuning = {},
            onRemoveTuning = {},
            onConfirm = {},
            onCancel = {},
        )
    }
}
