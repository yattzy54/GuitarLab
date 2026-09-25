package app.tuxguitar.android.view.dialog.measure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogFragment
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.measure.TGRemoveMeasureRangeAction
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong

class TGMeasureRemoveDialog : TGComposeBottomSheetDialogFragment() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val song = requireNotNull(getSong())
        val selection = requireNotNull(getHeader()).number

        TGMeasureRemoveDialogContent(
            values = createRangeValues(1, song.countMeasureHeaders()),
            initialFrom = selection,
            initialTo = selection,
            onSave = { fromMeasure, toMeasure ->
                processAction(fromMeasure, toMeasure)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    fun createRangeValues(minimum: Int, maximum: Int): List<Int> = (minimum..maximum).toList()

    fun processAction(measureNumber1: Int, measureNumber2: Int) {
        val processor = TGActionProcessor(findContext(), TGRemoveMeasureRangeAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGRemoveMeasureRangeAction.ATTRIBUTE_MEASURE_NUMBER_1, measureNumber1)
        processor.setAttribute(TGRemoveMeasureRangeAction.ATTRIBUTE_MEASURE_NUMBER_2, measureNumber2)
        processor.process()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getHeader(): TGMeasureHeader? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER)
}

@Composable
fun TGMeasureRemoveDialogContent(
    values: List<Int>,
    initialFrom: Int,
    initialTo: Int,
    onSave: (Int, Int) -> Unit,
    onCancel: () -> Unit,
) {
    var fromMeasure by remember { mutableStateOf(initialFrom) }
    var toMeasure by remember { mutableStateOf(initialTo) }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = stringResource(R.string.measure_remove_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        IntDropdownField(
            label = stringResource(R.string.measure_remove_dlg_from_label),
            options = values,
            selected = fromMeasure,
            onSelect = { selected -> fromMeasure = selected.coerceAtMost(toMeasure) },
        )
        IntDropdownField(
            label = stringResource(R.string.measure_remove_dlg_to_label),
            options = values,
            selected = toMeasure,
            onSelect = { selected -> toMeasure = selected.coerceAtLeast(fromMeasure) },
            modifier = Modifier.padding(top = 12.dp),
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
            TextButton(onClick = { onSave(fromMeasure, toMeasure) }) {
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
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
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
private fun TGMeasureRemoveDialogContentPreview() {
    MaterialTheme {
        TGMeasureRemoveDialogContent(
            values = (1..8).toList(),
            initialFrom = 2,
            initialTo = 4,
            onSave = { _, _ -> },
            onCancel = {},
        )
    }
}
