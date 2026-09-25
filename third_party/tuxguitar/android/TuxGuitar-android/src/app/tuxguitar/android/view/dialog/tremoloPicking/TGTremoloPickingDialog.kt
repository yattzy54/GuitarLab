package app.tuxguitar.android.view.dialog.tremoloPicking

import app.tuxguitar.android.ui.state.bind
import app.tuxguitar.android.ui.state.scopedEditorViewModel

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGDuration
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGNote
import app.tuxguitar.song.models.TGString
import app.tuxguitar.song.models.effects.TGEffectTremoloPicking

private data class TGTremoloPickingDurationOption(
    val value: Int,
    val label: String,
    val iconResId: Int,
)

class TGTremoloPickingDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val selectedDuration = getNote()?.takeIf { it.effect.isTremoloPicking }
            ?.effect?.tremoloPicking?.duration?.value ?: TGDuration.EIGHTH
        TGTremoloPickingDialogContent(
            options = createDurationOptions(),
            selectedDuration = selectedDuration,
            onSave = { duration ->
                updateEffect(createTremoloPicking(duration))
                onDismiss()
            },
            onClean = {
                updateEffect(null)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    private fun createDurationOptions(): List<TGTremoloPickingDurationOption> = listOf(
        TGTremoloPickingDurationOption(TGDuration.EIGHTH, getString(R.string.tremolo_picking_dlg_duration_8), R.drawable.duration_8),
        TGTremoloPickingDurationOption(TGDuration.SIXTEENTH, getString(R.string.tremolo_picking_dlg_duration_16), R.drawable.duration_16),
        TGTremoloPickingDurationOption(TGDuration.THIRTY_SECOND, getString(R.string.tremolo_picking_dlg_duration_32), R.drawable.duration_32),
    )

    fun createTremoloPicking(duration: Int): TGEffectTremoloPicking =
        getSongManager().factory.newEffectTremoloPicking().also {
            it.duration.setValue(duration)
        }

    fun updateEffect(effect: TGEffectTremoloPicking?) {
        val processor = TGActionProcessor(findContext(), TGChangeTremoloPickingAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, getString())
        processor.setAttribute(TGChangeTremoloPickingAction.ATTRIBUTE_EFFECT, effect)
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
private fun TGTremoloPickingDialogContent(
    options: List<TGTremoloPickingDurationOption>,
    selectedDuration: Int,
    onSave: (Int) -> Unit,
    onClean: () -> Unit,
    onCancel: () -> Unit,
) {
    val initialIndex = options.indexOfFirst { it.value == selectedDuration }.takeIf { it >= 0 } ?: 0
    val viewModel = scopedEditorViewModel { TGTremoloPickingDialogViewModel(TGTremoloPickingDialogState(initialIndex)) }
    var selectedIndex by viewModel.bind({ it.selectedIndex }, viewModel::onSelectedIndexChanged)

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = stringResource(R.string.tremolo_picking_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        options.forEachIndexed { index, option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = selectedIndex == index, onClick = { selectedIndex = index })
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = selectedIndex == index, onClick = { selectedIndex = index })
                Image(
                    painter = painterResource(option.iconResId),
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
                Text(option.label)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onClean) {
                Text(stringResource(R.string.global_button_clean))
            }
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.global_button_cancel))
            }
            TextButton(onClick = { onSave(options[selectedIndex].value) }) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGTremoloPickingDialogContentPreview() {
    GuitarLabTheme {
        TGTremoloPickingDialogContent(
            options = listOf(
                TGTremoloPickingDurationOption(TGDuration.EIGHTH, "Eighth", R.drawable.duration_8),
                TGTremoloPickingDurationOption(TGDuration.SIXTEENTH, "Sixteenth", R.drawable.duration_16),
                TGTremoloPickingDurationOption(TGDuration.THIRTY_SECOND, "Thirty-Second", R.drawable.duration_32),
            ),
            selectedDuration = TGDuration.SIXTEENTH,
            onSave = {},
            onClean = {},
            onCancel = {},
        )
    }
}
