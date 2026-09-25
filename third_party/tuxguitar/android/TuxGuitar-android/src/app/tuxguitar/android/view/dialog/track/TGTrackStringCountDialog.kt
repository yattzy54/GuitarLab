package app.tuxguitar.android.view.dialog.track

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogFragment
import app.tuxguitar.android.view.dialog.compose.TGDialogActionButtons
import app.tuxguitar.android.view.dialog.compose.TGDialogDropdownField
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.track.TGSetTrackStringCountAction
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTrack

class TGTrackStringCountDialog : TGComposeBottomSheetDialogFragment() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val values = createCountValues().toList()
        val selectedCount = getTrack()?.stringCount() ?: values.first()
        TGTrackStringCountDialogContent(
            values = values,
            selectedCount = selectedCount,
            onSave = { count ->
                updateStringCount(count)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    fun createCountValues(): Array<Int> =
        Array(TGTrack.MAX_STRINGS - TGTrack.MIN_STRINGS + 1) { TGTrack.MIN_STRINGS + it }

    fun updateStringCount(count: Int) {
        val processor = TGActionProcessor(findContext(), TGSetTrackStringCountAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, getTrack())
        processor.setAttribute(TGSetTrackStringCountAction.ATTRIBUTE_STRING_COUNT, count)
        processor.process()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getTrack(): TGTrack? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK)
}

@Composable
fun TGTrackStringCountDialogContent(
    values: List<Int>,
    selectedCount: Int,
    onSave: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    val initialIndex = values.indexOf(selectedCount).takeIf { it >= 0 } ?: 0
    var selectedIndex by remember(selectedCount, values) { mutableIntStateOf(initialIndex) }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = stringResource(R.string.track_string_count_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        TGDialogDropdownField(
            label = stringResource(R.string.track_string_count_dlg_count_label),
            selectedText = values[selectedIndex].toString(),
            options = values.map(Int::toString),
            onOptionSelected = { selectedIndex = it },
        )
        TGDialogActionButtons(
            onConfirm = { onSave(values[selectedIndex]) },
            onCancel = onCancel,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TGTrackStringCountDialogContentPreview() {
    MaterialTheme {
        TGTrackStringCountDialogContent(
            values = (4..8).toList(),
            selectedCount = 6,
            onSave = {},
            onCancel = {},
        )
    }
}
