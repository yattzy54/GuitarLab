package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.ui.state.bind
import app.tuxguitar.android.ui.state.scopedEditorViewModel

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.measure.TGPasteMeasureAction
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong

data class TGMeasurePasteModeOption(
    val mode: Int,
    val labelRes: Int,
)

class TGMeasurePasteDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val options = listOf(
            TGMeasurePasteModeOption(TRANSFER_TYPE_REPLACE, R.string.measure_paste_dlg_options_mode_replace),
            TGMeasurePasteModeOption(TRANSFER_TYPE_INSERT, R.string.measure_paste_dlg_options_mode_insert),
        )

        TGMeasurePasteDialogContent(
            countOptions = createCountValues(),
            initialCount = 1,
            modeOptions = options,
            initialMode = TRANSFER_TYPE_REPLACE,
            onSave = { count, mode ->
                processAction(count, mode)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    fun createCountValues(): List<Int> = (1..100).toList()

    fun processAction(count: Int, mode: Int) {
        val processor = TGActionProcessor(findContext(), TGPasteMeasureAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(ATTRIBUTE_PASTE_COUNT, count)
        processor.setAttribute(ATTRIBUTE_PASTE_MODE, mode)
        processor.process()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getHeader(): TGMeasureHeader? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER)

    companion object {
        const val ATTRIBUTE_PASTE_MODE = "pasteMode"
        const val ATTRIBUTE_PASTE_COUNT = "pasteCount"
        const val TRANSFER_TYPE_REPLACE = 1
        const val TRANSFER_TYPE_INSERT = 2
    }
}

@Composable
fun TGMeasurePasteDialogContent(
    countOptions: List<Int>,
    initialCount: Int,
    modeOptions: List<TGMeasurePasteModeOption>,
    initialMode: Int,
    onSave: (Int, Int) -> Unit,
    onCancel: () -> Unit,
) {
    val viewModel = scopedEditorViewModel { TGMeasurePasteDialogViewModel(TGMeasurePasteDialogState(initialCount, initialMode)) }
    var count by viewModel.bind({ it.count }, viewModel::onCountChanged)
    var mode by viewModel.bind({ it.mode }, viewModel::onModeChanged)

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = stringResource(R.string.measure_paste_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        IntDropdownField(
            label = stringResource(R.string.measure_paste_dlg_count_label),
            options = countOptions,
            selected = count,
            onSelect = { count = it },
        )

        Column(modifier = Modifier.padding(top = 16.dp)) {
            modeOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(selected = mode == option.mode, onClick = { mode = option.mode })
                        .padding(vertical = 4.dp),
                ) {
                    RadioButton(
                        selected = mode == option.mode,
                        onClick = { mode = option.mode },
                    )
                    Text(
                        text = stringResource(option.labelRes),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable { mode = option.mode },
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.global_button_cancel))
            }
            TextButton(onClick = { onSave(count, mode) }) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Composable
private fun IntDropdownField(
    label: String,
    options: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(selected.toString())
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.75f),
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.toString()) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGMeasurePasteDialogContentPreview() {
    GuitarLabTheme {
        TGMeasurePasteDialogContent(
            countOptions = (1..5).toList(),
            initialCount = 1,
            modeOptions = listOf(
                TGMeasurePasteModeOption(TGMeasurePasteDialog.TRANSFER_TYPE_REPLACE, R.string.measure_paste_dlg_options_mode_replace),
                TGMeasurePasteModeOption(TGMeasurePasteDialog.TRANSFER_TYPE_INSERT, R.string.measure_paste_dlg_options_mode_insert),
            ),
            initialMode = TGMeasurePasteDialog.TRANSFER_TYPE_REPLACE,
            onSave = { _, _ -> },
            onCancel = {},
        )
    }
}
