package app.tuxguitar.android.view.dialog.repeat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.composition.TGRepeatAlternativeAction
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong

class TGRepeatAlternativeDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val song = getSong()
        val header = getHeader()
        val existent = getExistentEndings(song, header)
        val selected = if (header.repeatAlternative > 0) {
            header.repeatAlternative
        } else {
            getDefaultEndings(existent)
        }

        TGRepeatAlternativeDialogContent(
            existentEndings = existent,
            initialSelectedEndings = selected,
            onSave = { repeatAlternative ->
                changeRepeatAlternative(repeatAlternative)
                onDismiss()
            },
            onClean = {
                changeRepeatAlternative(0)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    protected fun getExistentEndings(song: TGSong, measureHeader: TGMeasureHeader): Int {
        var existentEndings = 0
        val iterator = song.getMeasureHeaders()
        while (iterator.hasNext()) {
            val header = iterator.next()
            if (header.number == measureHeader.number) break
            if (header.isRepeatOpen) existentEndings = 0
            existentEndings = existentEndings or header.repeatAlternative
        }
        return existentEndings
    }

    protected fun getDefaultEndings(existentEndings: Int): Int {
        for (i in 0 until 8) {
            if (existentEndings and (1 shl i) == 0) return 1 shl i
        }
        return -1
    }

    fun changeRepeatAlternative(repeatAlternative: Int) {
        val processor = TGActionProcessor(findContext(), TGRepeatAlternativeAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, getHeader())
        processor.setAttribute(TGRepeatAlternativeAction.ATTRIBUTE_REPEAT_ALTERNATIVE, repeatAlternative)
        processor.processOnNewThread()
    }

    fun getSong(): TGSong = requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG))

    fun getHeader(): TGMeasureHeader =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER))
}

@Composable
fun TGRepeatAlternativeDialogContent(
    existentEndings: Int,
    initialSelectedEndings: Int,
    onSave: (Int) -> Unit,
    onClean: () -> Unit,
    onCancel: () -> Unit,
) {
    var selectedEndings by remember { mutableStateOf(initialSelectedEndings) }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = androidx.compose.ui.res.stringResource(R.string.repeat_alternative_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        for (rowIndex in 0 until 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (columnIndex in 0 until 4) {
                    val index = rowIndex * 4 + columnIndex
                    val bit = 1 shl index
                    val enabled = existentEndings and bit == 0
                    val checked = enabled && selectedEndings and bit != 0
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                if (enabled) {
                                    selectedEndings = if (isChecked) {
                                        selectedEndings or bit
                                    } else {
                                        selectedEndings and bit.inv()
                                    }
                                }
                            },
                            enabled = enabled,
                        )
                        Text((index + 1).toString())
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onClean) {
                Text(androidx.compose.ui.res.stringResource(R.string.global_button_clean))
            }
            TextButton(onClick = onCancel) {
                Text(androidx.compose.ui.res.stringResource(R.string.global_button_cancel))
            }
            TextButton(onClick = { onSave(selectedEndings.coerceAtLeast(0)) }) {
                Text(androidx.compose.ui.res.stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGRepeatAlternativeDialogContentPreview() {
    MaterialTheme {
        TGRepeatAlternativeDialogContent(
            existentEndings = 0b00000100,
            initialSelectedEndings = 0b00000001,
            onSave = {},
            onClean = {},
            onCancel = {},
        )
    }
}
