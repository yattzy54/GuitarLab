package app.tuxguitar.android.view.dialog.clef

import app.tuxguitar.android.ui.state.bind
import app.tuxguitar.android.ui.state.scopedEditorViewModel

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.composition.TGChangeClefAction
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTrack



private data class TGClefOption(
    val value: Int,
    val label: String,
)

class TGClefDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val measure = requireNotNull(getMeasure())
        TGClefDialogContent(
            initial = TGClefDialogUiState(
                clef = measure.clef,
                applyToEnd = true,
            ),
            options = createClefValues(),
            onSave = { state ->
                changeClef(state)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    private fun createClefValues(): List<TGClefOption> = listOf(
        TGClefOption(TGMeasure.CLEF_TREBLE, getString(R.string.clef_dlg_clef_value_treble)),
        TGClefOption(TGMeasure.CLEF_BASS, getString(R.string.clef_dlg_clef_value_bass)),
        TGClefOption(TGMeasure.CLEF_TENOR, getString(R.string.clef_dlg_clef_value_tenor)),
        TGClefOption(TGMeasure.CLEF_ALTO, getString(R.string.clef_dlg_clef_value_alto)),
    )

    fun changeClef(state: TGClefDialogUiState) {
        val processor = TGActionProcessor(findContext(), TGChangeClefAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, getTrack())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
        processor.setAttribute(TGChangeClefAction.ATTRIBUTE_CLEF, state.clef)
        processor.setAttribute(TGChangeClefAction.ATTRIBUTE_APPLY_TO_END, state.applyToEnd)
        processor.process()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getTrack(): TGTrack? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK)
    fun getMeasure(): TGMeasure? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE)
}

@Composable
private fun TGClefDialogContent(
    initial: TGClefDialogUiState,
    options: List<TGClefOption>,
    onSave: (TGClefDialogUiState) -> Unit,
    onCancel: () -> Unit,
) {
    val viewModel = scopedEditorViewModel { TGClefDialogViewModel(initial) }
    var clef by viewModel.bind({ it.clef }, viewModel::onClefChanged)
    var applyToEnd by viewModel.bind({ it.applyToEnd }, viewModel::onApplyToEndChanged)

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 480.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.clef_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        TGClefDropdownField(
            label = stringResource(R.string.clef_dlg_clef_label),
            options = options,
            selectedValue = clef,
            onValueSelected = { clef = it },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .clickable { applyToEnd = !applyToEnd },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = applyToEnd, onCheckedChange = { applyToEnd = it })
            Text(
                text = stringResource(R.string.clef_dlg_options_apply_to_end),
                modifier = Modifier.padding(start = 8.dp),
            )
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
            TextButton(onClick = { onSave(TGClefDialogUiState(clef = clef, applyToEnd = applyToEnd)) }) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Composable
private fun TGClefDropdownField(
    label: String,
    options: List<TGClefOption>,
    selectedValue: Int,
    onValueSelected: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.value == selectedValue }?.label.orEmpty()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = selectedLabel, modifier = Modifier.fillMaxWidth())
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.widthIn(min = 200.dp).heightIn(max = 280.dp),
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            onValueSelected(option.value)
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
private fun TGClefDialogContentPreview() {
    GuitarLabTheme {
        TGClefDialogContent(
            initial = TGClefDialogUiState(
                clef = TGMeasure.CLEF_TREBLE,
                applyToEnd = true,
            ),
            options = listOf(
                TGClefOption(TGMeasure.CLEF_TREBLE, "Treble"),
                TGClefOption(TGMeasure.CLEF_BASS, "Bass"),
                TGClefOption(TGMeasure.CLEF_TENOR, "Tenor"),
                TGClefOption(TGMeasure.CLEF_ALTO, "Alto"),
            ),
            onSave = {},
            onCancel = {},
        )
    }
}
