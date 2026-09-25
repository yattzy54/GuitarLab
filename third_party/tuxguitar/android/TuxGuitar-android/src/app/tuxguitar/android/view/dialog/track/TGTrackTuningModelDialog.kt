package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.ui.state.bind
import app.tuxguitar.android.ui.state.scopedEditorViewModel

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.android.view.dialog.compose.TGDialogActionButtons
import app.tuxguitar.android.view.dialog.compose.TGDialogDropdownField
import app.tuxguitar.android.view.util.TGSelectableItem

class TGTrackTuningModelDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val tunings = createSelectableTunings()
        val currentValue = getModel()?.value
        val selectedIndex = tunings.indexOfFirst { (it.getItem() as? Int) == currentValue }
            .takeIf { it >= 0 } ?: 0

        TGTrackTuningModelDialogContent(
            tuningValues = tunings.map { it.getItem() as Int },
            tuningLabels = tunings.map { it.getLabel().orEmpty() },
            selectedIndex = selectedIndex,
            onSelect = { index ->
                handleSelection(tunings[index].getItem() as Int)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    fun getModel(): TGTrackTuningModel? =
        getAttribute(TGTrackTuningModelDialogController.ATTRIBUTE_MODEL)

    fun createSelectableTunings(): List<TGSelectableItem> =
        (0 until MAX_OCTAVES * TGTrackTuningLabel.KEY_NAMES.size).map { value ->
            TGSelectableItem(value, TGTrackTuningLabel.valueOf(value, true))
        }

    fun handleSelection(selectedValue: Int): Boolean {
        val model = TGTrackTuningModel().apply { value = selectedValue }
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

@Composable
fun TGTrackTuningModelDialogContent(
    tuningValues: List<Int>,
    tuningLabels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    val viewModel = scopedEditorViewModel { TGTrackTuningModelDialogViewModel(TGTrackTuningModelDialogState(selectedIndex.coerceIn(0, tuningValues.lastIndex))) }
    var currentIndex by viewModel.bind({ it.currentIndex }, viewModel::onCurrentIndexChanged)

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = stringResource(R.string.track_tuning_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        TGDialogDropdownField(
            label = stringResource(R.string.track_tuning_dlg_value_label),
            selectedText = tuningLabels[currentIndex],
            options = tuningLabels,
            onOptionSelected = { currentIndex = it },
        )
        OutlinedTextField(
            value = TGTrackTuningLabel.valueOf(tuningValues[currentIndex]),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            label = { Text(stringResource(R.string.track_tuning_dlg_preview_label)) },
            readOnly = true,
            enabled = false,
            singleLine = true,
        )
        TGDialogActionButtons(
            onConfirm = { onSelect(currentIndex) },
            onCancel = onCancel,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TGTrackTuningModelDialogContentPreview() {
    GuitarLabTheme {
        TGTrackTuningModelDialogContent(
            tuningValues = listOf(48, 49, 50, 51),
            tuningLabels = listOf("C4", "C#4", "D4", "D#4"),
            selectedIndex = 2,
            onSelect = {},
            onCancel = {},
        )
    }
}
