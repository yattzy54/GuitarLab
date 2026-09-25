package app.tuxguitar.android.view.dialog.stroke

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
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
import app.tuxguitar.editor.action.note.TGChangeStrokeAction
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGDuration
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGStroke

data class TGStrokeDialogUiState(
    val direction: Int,
    val duration: Int,
)

private data class TGStrokeOption(
    val value: Int,
    val label: String,
)

private data class TGStrokeDurationOption(
    val value: Int,
    val labelRes: Int,
)

class TGStrokeDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val beat = getBeat()
        val stroke = beat?.stroke
        val direction = stroke?.direction ?: TGStroke.STROKE_NONE
        val duration = if (stroke != null && direction != TGStroke.STROKE_NONE) {
            stroke.value
        } else {
            TGDuration.SIXTEENTH
        }
        TGStrokeDialogContent(
            initial = TGStrokeDialogUiState(direction = direction, duration = duration),
            directionOptions = createDirectionValues(),
            onSave = { state ->
                processAction(state)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    private fun createDirectionValues(): List<TGStrokeOption> = listOf(
        TGStrokeOption(TGStroke.STROKE_NONE, getString(R.string.stroke_dlg_direction_none)),
        TGStrokeOption(TGStroke.STROKE_UP, getString(R.string.stroke_dlg_direction_up)),
        TGStrokeOption(TGStroke.STROKE_DOWN, getString(R.string.stroke_dlg_direction_down)),
    )

    fun processAction(state: TGStrokeDialogUiState) {
        val processor = TGActionProcessor(findContext(), TGChangeStrokeAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
        processor.setAttribute(TGChangeStrokeAction.ATTRIBUTE_STROKE_DIRECTION, state.direction)
        processor.setAttribute(TGChangeStrokeAction.ATTRIBUTE_STROKE_VALUE, state.duration)
        processor.process()
    }

    fun getMeasure(): TGMeasure? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE)
    fun getBeat(): TGBeat? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT)
}

@Composable
private fun TGStrokeDialogContent(
    initial: TGStrokeDialogUiState,
    directionOptions: List<TGStrokeOption>,
    onSave: (TGStrokeDialogUiState) -> Unit,
    onCancel: () -> Unit,
) {
    val durationOptions = listOf(
        TGStrokeDurationOption(TGDuration.QUARTER, R.string.stroke_dlg_duration_4),
        TGStrokeDurationOption(TGDuration.EIGHTH, R.string.stroke_dlg_duration_8),
        TGStrokeDurationOption(TGDuration.SIXTEENTH, R.string.stroke_dlg_duration_16),
        TGStrokeDurationOption(TGDuration.THIRTY_SECOND, R.string.stroke_dlg_duration_32),
        TGStrokeDurationOption(TGDuration.SIXTY_FOURTH, R.string.stroke_dlg_duration_64),
    )
    var direction by remember(initial.direction) { mutableStateOf(initial.direction) }
    var duration by remember(initial.duration) { mutableStateOf(initial.duration) }
    val durationsEnabled = direction != TGStroke.STROKE_NONE

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 480.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.stroke_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        TGStrokeDropdownField(
            label = stringResource(R.string.stroke_dlg_direction_label),
            options = directionOptions,
            selectedValue = direction,
            onValueSelected = { direction = it },
        )

        durationOptions.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = duration == option.value,
                        enabled = durationsEnabled,
                        onClick = { duration = option.value },
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = duration == option.value,
                    onClick = { duration = option.value },
                    enabled = durationsEnabled,
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
            TextButton(
                onClick = {
                    onSave(
                        TGStrokeDialogUiState(
                            direction = direction,
                            duration = duration,
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
private fun TGStrokeDropdownField(
    label: String,
    options: List<TGStrokeOption>,
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
private fun TGStrokeDialogContentPreview() {
    GuitarLabTheme {
        TGStrokeDialogContent(
            initial = TGStrokeDialogUiState(
                direction = TGStroke.STROKE_UP,
                duration = TGDuration.SIXTEENTH,
            ),
            directionOptions = listOf(
                TGStrokeOption(TGStroke.STROKE_NONE, "None"),
                TGStrokeOption(TGStroke.STROKE_UP, "Up"),
                TGStrokeOption(TGStroke.STROKE_DOWN, "Down"),
            ),
            onSave = {},
            onCancel = {},
        )
    }
}
