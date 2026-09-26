package app.tuxguitar.android.view.dialog.keySignature

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
import app.tuxguitar.editor.action.composition.TGChangeKeySignatureAction
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTrack



private data class TGKeySignatureOption(
    val value: Int,
    val label: String,
)

class TGKeySignatureDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val measure = requireNotNull(getMeasure())
        TGKeySignatureDialogContent(
            initial = TGKeySignatureDialogUiState(
                keySignature = measure.keySignature,
                applyToEnd = true,
            ),
            options = createKeyValues(),
            onSave = { state ->
                changeKeySignature(state)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    private fun createKeyValues(): List<TGKeySignatureOption> = listOf(
        TGKeySignatureOption(0, getString(R.string.key_signature_dlg_ks_value_natural)),
        TGKeySignatureOption(1, getString(R.string.key_signature_dlg_ks_value_sharp_1)),
        TGKeySignatureOption(2, getString(R.string.key_signature_dlg_ks_value_sharp_2)),
        TGKeySignatureOption(3, getString(R.string.key_signature_dlg_ks_value_sharp_3)),
        TGKeySignatureOption(4, getString(R.string.key_signature_dlg_ks_value_sharp_4)),
        TGKeySignatureOption(5, getString(R.string.key_signature_dlg_ks_value_sharp_5)),
        TGKeySignatureOption(6, getString(R.string.key_signature_dlg_ks_value_sharp_6)),
        TGKeySignatureOption(7, getString(R.string.key_signature_dlg_ks_value_sharp_7)),
        TGKeySignatureOption(8, getString(R.string.key_signature_dlg_ks_value_flat_1)),
        TGKeySignatureOption(9, getString(R.string.key_signature_dlg_ks_value_flat_2)),
        TGKeySignatureOption(10, getString(R.string.key_signature_dlg_ks_value_flat_3)),
        TGKeySignatureOption(11, getString(R.string.key_signature_dlg_ks_value_flat_4)),
        TGKeySignatureOption(12, getString(R.string.key_signature_dlg_ks_value_flat_5)),
        TGKeySignatureOption(13, getString(R.string.key_signature_dlg_ks_value_flat_6)),
        TGKeySignatureOption(14, getString(R.string.key_signature_dlg_ks_value_flat_7)),
    )

    fun changeKeySignature(state: TGKeySignatureDialogUiState) {
        val processor = TGActionProcessor(findContext(), TGChangeKeySignatureAction.NAME)
        processor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_KEY_SIGNATURE, state.keySignature)
        processor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_END, state.applyToEnd)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, getTrack())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
        processor.processOnNewThread()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getTrack(): TGTrack? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK)
    fun getMeasure(): TGMeasure? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE)
}

@Composable
private fun TGKeySignatureDialogContent(
    initial: TGKeySignatureDialogUiState,
    options: List<TGKeySignatureOption>,
    onSave: (TGKeySignatureDialogUiState) -> Unit,
    onCancel: () -> Unit,
) {
    val viewModel = scopedEditorViewModel { TGKeySignatureDialogViewModel(initial) }
    var keySignature by viewModel.bind({ it.keySignature }, viewModel::onKeySignatureChanged)
    var applyToEnd by viewModel.bind({ it.applyToEnd }, viewModel::onApplyToEndChanged)

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 480.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.key_signature_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        TGKeySignatureDropdownField(
            label = stringResource(R.string.key_signature_dlg_ks_label),
            options = options,
            selectedValue = keySignature,
            onValueSelected = { keySignature = it },
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
                text = stringResource(R.string.key_signature_dlg_options_apply_to_end),
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
                        TGKeySignatureDialogUiState(
                            keySignature = keySignature,
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
private fun TGKeySignatureDropdownField(
    label: String,
    options: List<TGKeySignatureOption>,
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
private fun TGKeySignatureDialogContentPreview() {
    GuitarLabTheme {
        TGKeySignatureDialogContent(
            initial = TGKeySignatureDialogUiState(
                keySignature = 2,
                applyToEnd = true,
            ),
            options = listOf(
                TGKeySignatureOption(0, "Natural"),
                TGKeySignatureOption(1, "1 sharp"),
                TGKeySignatureOption(8, "1 flat"),
            ),
            onSave = {},
            onCancel = {},
        )
    }
}
