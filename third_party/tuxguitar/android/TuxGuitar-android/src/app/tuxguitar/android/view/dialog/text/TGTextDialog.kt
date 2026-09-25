package app.tuxguitar.android.view.dialog.text

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import app.tuxguitar.editor.action.note.TGInsertTextAction
import app.tuxguitar.editor.action.note.TGRemoveTextAction
import app.tuxguitar.song.models.TGBeat

class TGTextDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        TGTextDialogContent(
            initialText = getBeat()?.text?.value.orEmpty(),
            onSave = { text ->
                doInsertText(text)
                onDismiss()
            },
            onClean = {
                doRemoveText()
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    fun doInsertText(text: String) {
        val processor = TGActionProcessor(findContext(), TGInsertTextAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
        processor.setAttribute(TGInsertTextAction.ATTRIBUTE_TEXT_VALUE, text)
        processor.process()
    }

    fun doRemoveText() {
        val processor = TGActionProcessor(findContext(), TGRemoveTextAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
        processor.process()
    }

    fun getBeat(): TGBeat? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT)
}

@Composable
private fun TGTextDialogContent(
    initialText: String,
    onSave: (String) -> Unit,
    onClean: () -> Unit,
    onCancel: () -> Unit,
) {
    var text by remember(initialText) { mutableStateOf(initialText) }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = stringResource(R.string.text_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
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
            TextButton(onClick = onClean) {
                Text(stringResource(R.string.global_button_clean))
            }
            TextButton(onClick = { onSave(text) }) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGTextDialogContentPreview() {
    GuitarLabTheme {
        TGTextDialogContent(
            initialText = "let ring",
            onSave = {},
            onClean = {},
            onCancel = {},
        )
    }
}
