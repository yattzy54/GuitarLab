package app.tuxguitar.android.view.dialog.tripletFeel

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
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
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.composition.TGChangeTripletFeelAction
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong

data class TGTripletFeelDialogUiState(
    val tripletFeel: Int,
    val applyToEnd: Boolean,
)

private data class TGTripletFeelOption(
    val value: Int,
    val labelRes: Int,
)

class TGTripletFeelDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val header = getHeader()
        TGTripletFeelDialogContent(
            initial = TGTripletFeelDialogUiState(
                tripletFeel = header.tripletFeel,
                applyToEnd = true,
            ),
            onSave = { state ->
                changeTripletFeel(state)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    fun changeTripletFeel(state: TGTripletFeelDialogUiState) {
        val processor = TGActionProcessor(findContext(), TGChangeTripletFeelAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, getHeader())
        processor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_TRIPLET_FEEL, state.tripletFeel)
        processor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_APPLY_TO_END, state.applyToEnd)
        processor.processOnNewThread()
    }

    fun getSong(): TGSong =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG))

    fun getHeader(): TGMeasureHeader =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER))
}

@Composable
private fun TGTripletFeelDialogContent(
    initial: TGTripletFeelDialogUiState,
    onSave: (TGTripletFeelDialogUiState) -> Unit,
    onCancel: () -> Unit,
) {
    val options = listOf(
        TGTripletFeelOption(TGMeasureHeader.TRIPLET_FEEL_NONE, R.string.triplet_feel_dlg_none),
        TGTripletFeelOption(TGMeasureHeader.TRIPLET_FEEL_EIGHTH, R.string.triplet_feel_dlg_eighth),
        TGTripletFeelOption(TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH, R.string.triplet_feel_dlg_sixteenth),
    )
    var tripletFeel by remember(initial.tripletFeel) { mutableStateOf(initial.tripletFeel) }
    var applyToEnd by remember(initial.applyToEnd) { mutableStateOf(initial.applyToEnd) }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 480.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.triplet_feel_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = tripletFeel == option.value,
                        onClick = { tripletFeel = option.value },
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = tripletFeel == option.value,
                    onClick = { tripletFeel = option.value },
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
                .padding(top = 16.dp)
                .clickable { applyToEnd = !applyToEnd },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = applyToEnd, onCheckedChange = { applyToEnd = it })
            Text(
                text = stringResource(R.string.triplet_feel_dlg_options_apply_to_end),
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
                        TGTripletFeelDialogUiState(
                            tripletFeel = tripletFeel,
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

@Preview(showBackground = true)
@Composable
private fun TGTripletFeelDialogContentPreview() {
    GuitarLabTheme {
        TGTripletFeelDialogContent(
            initial = TGTripletFeelDialogUiState(
                tripletFeel = TGMeasureHeader.TRIPLET_FEEL_EIGHTH,
                applyToEnd = true,
            ),
            onSave = {},
            onCancel = {},
        )
    }
}
