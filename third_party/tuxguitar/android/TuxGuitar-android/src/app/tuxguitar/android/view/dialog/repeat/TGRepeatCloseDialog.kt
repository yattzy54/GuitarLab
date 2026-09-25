package app.tuxguitar.android.view.dialog.repeat

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
import app.tuxguitar.editor.action.composition.TGRepeatCloseAction
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong

class TGRepeatCloseDialog : TGComposeBottomSheetDialogFragment() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val repeatCloseDefault = getHeader().repeatClose.coerceAtLeast(1)
        TGRepeatCloseDialogContent(
            values = createRepeatValues(),
            initialCount = repeatCloseDefault.coerceIn(0, 100),
            onSave = { repeatCount ->
                changeRepeatClose(repeatCount)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    fun createRepeatValues(): List<Int> = (0..100).toList()

    fun changeRepeatClose(repeatCount: Int) {
        val processor = TGActionProcessor(findContext(), TGRepeatCloseAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, getHeader())
        processor.setAttribute(TGRepeatCloseAction.ATTRIBUTE_REPEAT_COUNT, repeatCount)
        processor.processOnNewThread()
    }

    fun getSong(): TGSong = requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG))

    fun getHeader(): TGMeasureHeader =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER))
}

@Composable
fun TGRepeatCloseDialogContent(
    values: List<Int>,
    initialCount: Int,
    onSave: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    var repeatCount by remember { mutableStateOf(initialCount) }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = stringResource(R.string.repeat_close_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        IntDropdownField(
            label = stringResource(R.string.repeat_close_dlg_count_label),
            options = values,
            selected = repeatCount,
            onSelect = { repeatCount = it },
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
            TextButton(onClick = { onSave(repeatCount) }) {
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
private fun TGRepeatCloseDialogContentPreview() {
    MaterialTheme {
        TGRepeatCloseDialogContent(
            values = (0..8).toList(),
            initialCount = 3,
            onSave = {},
            onCancel = {},
        )
    }
}
