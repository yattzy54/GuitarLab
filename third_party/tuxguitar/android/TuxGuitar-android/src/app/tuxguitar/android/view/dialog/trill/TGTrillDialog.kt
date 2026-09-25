package app.tuxguitar.android.view.dialog.trill

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogFragment
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.effect.TGChangeTrillNoteAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGDuration
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGNote
import app.tuxguitar.song.models.TGString
import app.tuxguitar.song.models.effects.TGEffectTrill

data class TGTrillDialogUiState(
    val fret: Int,
    val duration: Int?,
)

private data class TGTrillDurationOption(
    val value: Int,
    val labelRes: Int,
)

private data class TGTrillFretOption(
    val value: Int,
    val label: String,
)

class TGTrillDialog : TGComposeBottomSheetDialogFragment() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val note = getNote()
        val initialFret = if (note != null) {
            if (note.effect.isTrill) note.effect.trill.fret else note.value
        } else {
            0
        }
        val initialDuration = if (note != null && note.effect.isTrill) {
            note.effect.trill.duration.value
        } else {
            null
        }
        TGTrillDialogContent(
            initial = TGTrillDialogUiState(
                fret = initialFret,
                duration = initialDuration,
            ),
            fretOptions = createFretValues(),
            onSave = { state ->
                updateEffect(createTrill(state))
                onDismiss()
            },
            onClean = {
                cleanEffect()
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    private fun createFretValues(): List<TGTrillFretOption> =
        (0..100).map { TGTrillFretOption(it, it.toString()) }

    fun createTrill(state: TGTrillDialogUiState): TGEffectTrill =
        getSongManager().factory.newEffectTrill().also {
            it.setFret(state.fret)
            it.duration.setValue(state.duration ?: TGDuration.EIGHTH)
        }

    fun cleanEffect() {
        updateEffect(null)
    }

    fun updateEffect(effect: TGEffectTrill?) {
        val processor = TGActionProcessor(findContext(), TGChangeTrillNoteAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, getString())
        processor.setAttribute(TGChangeTrillNoteAction.ATTRIBUTE_EFFECT, effect)
        processor.process()
    }

    fun getSongManager(): TGSongManager =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER))

    fun getMeasure(): TGMeasure? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE)
    fun getBeat(): TGBeat? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT)
    fun getNote(): TGNote? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE)
    fun getString(): TGString? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING)
}

@Composable
private fun TGTrillDialogContent(
    initial: TGTrillDialogUiState,
    fretOptions: List<TGTrillFretOption>,
    onSave: (TGTrillDialogUiState) -> Unit,
    onClean: () -> Unit,
    onCancel: () -> Unit,
) {
    val durationOptions = listOf(
        TGTrillDurationOption(TGDuration.SIXTEENTH, R.string.trill_dlg_duration_16),
        TGTrillDurationOption(TGDuration.THIRTY_SECOND, R.string.trill_dlg_duration_32),
        TGTrillDurationOption(TGDuration.SIXTY_FOURTH, R.string.trill_dlg_duration_64),
    )
    var fret by remember(initial.fret) { mutableStateOf(initial.fret) }
    var duration by remember(initial.duration) { mutableStateOf(initial.duration) }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 480.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.trill_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        TGTrillFretDropdownField(
            label = stringResource(R.string.trill_dlg_fret_label),
            options = fretOptions,
            selectedValue = fret,
            onValueSelected = { fret = it },
        )

        durationOptions.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = duration == option.value,
                        onClick = { duration = option.value },
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = duration == option.value,
                    onClick = { duration = option.value },
                )
                Text(
                    text = stringResource(option.labelRes),
                    modifier = Modifier.padding(start = 8.dp),
                )
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
            TextButton(onClick = onClean) {
                Text(stringResource(R.string.global_button_clean))
            }
            TextButton(onClick = { onSave(TGTrillDialogUiState(fret = fret, duration = duration)) }) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Composable
private fun TGTrillFretDropdownField(
    label: String,
    options: List<TGTrillFretOption>,
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
private fun TGTrillDialogContentPreview() {
    MaterialTheme {
        TGTrillDialogContent(
            initial = TGTrillDialogUiState(
                fret = 7,
                duration = TGDuration.SIXTEENTH,
            ),
            fretOptions = (0..12).map { TGTrillFretOption(it, it.toString()) },
            onSave = {},
            onClean = {},
            onCancel = {},
        )
    }
}
