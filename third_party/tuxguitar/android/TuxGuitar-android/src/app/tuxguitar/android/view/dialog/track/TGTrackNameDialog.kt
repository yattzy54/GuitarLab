package app.tuxguitar.android.view.dialog.track

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import app.tuxguitar.android.view.dialog.compose.TGDialogActionButtons
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.track.TGSetTrackNameAction
import app.tuxguitar.song.models.TGTrack

class TGTrackNameDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        TGTrackNameDialogContent(
            initialName = getTrack()?.name.orEmpty(),
            onSave = { name ->
                updateTrackName(name)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    fun updateTrackName(name: String) {
        val processor = TGActionProcessor(findContext(), TGSetTrackNameAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, getTrack())
        processor.setAttribute(TGSetTrackNameAction.ATTRIBUTE_TRACK_NAME, name)
        processor.processOnNewThread()
    }

    fun getTrack(): TGTrack? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK)
}

@Composable
fun TGTrackNameDialogContent(
    initialName: String,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
) {
    var name by remember(initialName) { mutableStateOf(initialName) }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = stringResource(R.string.track_name_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.track_name_dlg_name_label)) },
            singleLine = true,
        )
        TGDialogActionButtons(
            onConfirm = { onSave(name) },
            onCancel = onCancel,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TGTrackNameDialogContentPreview() {
    GuitarLabTheme {
        TGTrackNameDialogContent(
            initialName = "Lead Guitar",
            onSave = {},
            onCancel = {},
        )
    }
}
