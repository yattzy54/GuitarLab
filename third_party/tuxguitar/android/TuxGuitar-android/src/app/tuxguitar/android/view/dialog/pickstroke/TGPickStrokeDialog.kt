package app.tuxguitar.android.view.dialog.pickstroke

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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.note.TGChangePickStrokeDownAction
import app.tuxguitar.editor.action.note.TGChangePickStrokeUpAction
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGPickStroke

private data class TGPickStrokeOption(
    val value: Int,
    val label: String,
)

class TGPickStrokeDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val beat = getBeat()
        TGPickStrokeDialogContent(
            initialDirection = beat?.pickStroke?.direction ?: TGPickStroke.PICK_STROKE_NONE,
            options = createDirectionValues(),
            onSave = { direction ->
                processAction(direction)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    private fun createDirectionValues(): List<TGPickStrokeOption> = listOf(
        TGPickStrokeOption(TGPickStroke.PICK_STROKE_NONE, getString(R.string.pickstroke_dlg_direction_none)),
        TGPickStrokeOption(TGPickStroke.PICK_STROKE_UP, getString(R.string.pickstroke_dlg_direction_up)),
        TGPickStrokeOption(TGPickStroke.PICK_STROKE_DOWN, getString(R.string.pickstroke_dlg_direction_down)),
    )

    fun processAction(direction: Int) {
        if (direction != TGPickStroke.PICK_STROKE_NONE) {
            val action = if (direction == TGPickStroke.PICK_STROKE_UP) {
                TGChangePickStrokeUpAction.NAME
            } else {
                TGChangePickStrokeDownAction.NAME
            }
            TGActionProcessor(findContext(), action).also {
                it.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
                it.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
                it.process()
            }
        }
    }

    fun getMeasure(): TGMeasure? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE)
    fun getBeat(): TGBeat? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT)
}

@Composable
private fun TGPickStrokeDialogContent(
    initialDirection: Int,
    options: List<TGPickStrokeOption>,
    onSave: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    var direction by remember(initialDirection) { mutableStateOf(initialDirection) }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 480.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.pickstroke_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        TGPickStrokeDropdownField(
            label = stringResource(R.string.pickstroke_dlg_direction_label),
            options = options,
            selectedValue = direction,
            onValueSelected = { direction = it },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.global_button_cancel))
            }
            TextButton(onClick = { onSave(direction) }) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Composable
private fun TGPickStrokeDropdownField(
    label: String,
    options: List<TGPickStrokeOption>,
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
private fun TGPickStrokeDialogContentPreview() {
    GuitarLabTheme {
        TGPickStrokeDialogContent(
            initialDirection = TGPickStroke.PICK_STROKE_UP,
            options = listOf(
                TGPickStrokeOption(TGPickStroke.PICK_STROKE_NONE, "None"),
                TGPickStrokeOption(TGPickStroke.PICK_STROKE_UP, "Up"),
                TGPickStrokeOption(TGPickStroke.PICK_STROKE_DOWN, "Down"),
            ),
            onSave = {},
            onCancel = {},
        )
    }
}
