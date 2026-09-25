package app.tuxguitar.android.view.dialog.measure

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
import app.tuxguitar.editor.action.measure.TGAddMeasureListAction
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong

data class TGMeasureAddOption(
    val measureNumber: Int,
    val labelRes: Int,
)

class TGMeasureAddDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val song = requireNotNull(getSong())
        val header = requireNotNull(getHeader())
        val options = listOf(
            TGMeasureAddOption(header.number, R.string.measure_add_dlg_options_before_position),
            TGMeasureAddOption(header.number + 1, R.string.measure_add_dlg_options_after_position),
            TGMeasureAddOption(song.countMeasureHeaders() + 1, R.string.measure_add_dlg_options_at_end),
        )

        TGMeasureAddDialogContent(
            countOptions = createCountValues(),
            initialCount = 1,
            options = options,
            initialMeasureNumber = options.last().measureNumber,
            onSave = { count, measureNumber ->
                processAction(count, measureNumber)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    fun createCountValues(): List<Int> = (1..100).toList()

    fun processAction(count: Int, measureNumber: Int) {
        val processor = TGActionProcessor(findContext(), TGAddMeasureListAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_COUNT, count)
        processor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_NUMBER, measureNumber)
        processor.process()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getHeader(): TGMeasureHeader? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER)
}

@Composable
fun TGMeasureAddDialogContent(
    countOptions: List<Int>,
    initialCount: Int,
    options: List<TGMeasureAddOption>,
    initialMeasureNumber: Int,
    onSave: (Int, Int) -> Unit,
    onCancel: () -> Unit,
) {
    var count by remember { mutableStateOf(initialCount) }
    var measureNumber by remember { mutableStateOf(initialMeasureNumber) }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = stringResource(R.string.measure_add_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        IntDropdownField(
            label = stringResource(R.string.measure_add_dlg_count_label),
            options = countOptions,
            selected = count,
            onSelect = { count = it },
        )

        Column(modifier = Modifier.padding(top = 16.dp)) {
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = measureNumber == option.measureNumber,
                            onClick = { measureNumber = option.measureNumber },
                        )
                        .padding(vertical = 4.dp),
                ) {
                    RadioButton(
                        selected = measureNumber == option.measureNumber,
                        onClick = { measureNumber = option.measureNumber },
                    )
                    Text(
                        text = stringResource(option.labelRes),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable { measureNumber = option.measureNumber },
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
            TextButton(onClick = { onSave(count, measureNumber) }) {
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
private fun TGMeasureAddDialogContentPreview() {
    GuitarLabTheme {
        TGMeasureAddDialogContent(
            countOptions = (1..5).toList(),
            initialCount = 1,
            options = listOf(
                TGMeasureAddOption(3, R.string.measure_add_dlg_options_before_position),
                TGMeasureAddOption(4, R.string.measure_add_dlg_options_after_position),
                TGMeasureAddOption(13, R.string.measure_add_dlg_options_at_end),
            ),
            initialMeasureNumber = 13,
            onSave = { _, _ -> },
            onCancel = {},
        )
    }
}
