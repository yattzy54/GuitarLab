package app.tuxguitar.android.view.dialog.timeSignature

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
import app.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTimeSignature



private data class TGTimeSignatureOption(
    val value: Int,
    val label: String,
)

class TGTimeSignatureDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val header = requireNotNull(getHeader())
        val timeSignature = header.timeSignature
        TGTimeSignatureDialogContent(
            initial = TGTimeSignatureDialogUiState(
                numerator = timeSignature.numerator,
                denominator = timeSignature.denominator.value,
                applyToEnd = true,
            ),
            numeratorOptions = createNumeratorValues(),
            denominatorOptions = createDenominatorValues(),
            onSave = { state ->
                changeTimeSignature(state)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    private fun createNumeratorValues(): List<TGTimeSignatureOption> =
        (1..32).map { TGTimeSignatureOption(it, it.toString()) }

    private fun createDenominatorValues(): List<TGTimeSignatureOption> {
        val values = mutableListOf<TGTimeSignatureOption>()
        var value = 1
        while (value <= 32) {
            values.add(TGTimeSignatureOption(value, value.toString()))
            value *= 2
        }
        return values
    }

    fun createTimeSignature(state: TGTimeSignatureDialogUiState): TGTimeSignature =
        getSongManager().factory.newTimeSignature().also {
            it.setNumerator(state.numerator)
            it.denominator.setValue(state.denominator)
        }

    fun changeTimeSignature(state: TGTimeSignatureDialogUiState) {
        val processor = TGActionProcessor(findContext(), TGChangeTimeSignatureAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, getHeader())
        processor.setAttribute(
            TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE,
            createTimeSignature(state),
        )
        processor.setAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_APPLY_TO_END, state.applyToEnd)
        processor.processOnNewThread()
    }

    fun getSongManager(): TGSongManager =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER))

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getHeader(): TGMeasureHeader? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER)
}

@Composable
private fun TGTimeSignatureDialogContent(
    initial: TGTimeSignatureDialogUiState,
    numeratorOptions: List<TGTimeSignatureOption>,
    denominatorOptions: List<TGTimeSignatureOption>,
    onSave: (TGTimeSignatureDialogUiState) -> Unit,
    onCancel: () -> Unit,
) {
    val viewModel = scopedEditorViewModel { TGTimeSignatureDialogViewModel(initial) }
    var numerator by viewModel.bind({ it.numerator }, viewModel::onNumeratorChanged)
    var denominator by viewModel.bind({ it.denominator }, viewModel::onDenominatorChanged)
    var applyToEnd by viewModel.bind({ it.applyToEnd }, viewModel::onApplyToEndChanged)

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 480.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.time_signature_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        TGTimeSignatureDropdownField(
            label = stringResource(R.string.time_signature_dlg_ts_numerator_label),
            options = numeratorOptions,
            selectedValue = numerator,
            onValueSelected = { numerator = it },
        )
        TGTimeSignatureDropdownField(
            label = stringResource(R.string.time_signature_dlg_ts_denominator_label),
            options = denominatorOptions,
            selectedValue = denominator,
            onValueSelected = { denominator = it },
            modifier = Modifier.padding(top = 12.dp),
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
                text = stringResource(R.string.time_signature_dlg_options_apply_to_end),
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
            TextButton(
                onClick = {
                    onSave(
                        TGTimeSignatureDialogUiState(
                            numerator = numerator,
                            denominator = denominator,
                            applyToEnd = applyToEnd,
                        )
                    )
                },
            ) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Composable
private fun TGTimeSignatureDropdownField(
    label: String,
    options: List<TGTimeSignatureOption>,
    selectedValue: Int,
    onValueSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.value == selectedValue }?.label.orEmpty()

    Column(modifier = modifier.fillMaxWidth()) {
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
private fun TGTimeSignatureDialogContentPreview() {
    GuitarLabTheme {
        TGTimeSignatureDialogContent(
            initial = TGTimeSignatureDialogUiState(
                numerator = 4,
                denominator = 4,
                applyToEnd = true,
            ),
            numeratorOptions = (1..8).map { TGTimeSignatureOption(it, it.toString()) },
            denominatorOptions = listOf(
                TGTimeSignatureOption(1, "1"),
                TGTimeSignatureOption(2, "2"),
                TGTimeSignatureOption(4, "4"),
                TGTimeSignatureOption(8, "8"),
            ),
            onSave = {},
            onCancel = {},
        )
    }
}
