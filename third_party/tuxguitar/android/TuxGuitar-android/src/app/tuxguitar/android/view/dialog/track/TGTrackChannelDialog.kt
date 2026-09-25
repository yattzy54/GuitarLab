package app.tuxguitar.android.view.dialog.track

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction
import app.tuxguitar.android.fragment.impl.TGChannelListFragmentController
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.android.view.dialog.compose.TGDialogActionButtons
import app.tuxguitar.android.view.dialog.compose.TGDialogDropdownField
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.track.TGSetTrackChannelAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGChannel
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTrack

class TGTrackChannelDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val songManager = requireNotNull(getSongManager())
        val song = requireNotNull(getSong())
        val track = requireNotNull(getTrack())
        val channels = createSelectableChannels(songManager, song)
        val selectedChannel = songManager.getChannel(song, track.getChannelId())
        val selectedIndex = channels.indexOf(TGSelectableItem(selectedChannel, null)).takeIf { it >= 0 } ?: 0

        TGTrackChannelDialogContent(
            channelLabels = channels.map { it.getLabel().orEmpty() },
            selectedIndex = selectedIndex,
            onOpenInstruments = { openConfigureInstruments() },
            onSave = { index ->
                updateTrackInstrument(track, channels.getOrNull(index))
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    fun createSelectableChannels(songManager: TGSongManager, song: TGSong): List<TGSelectableItem> =
        buildList {
            add(TGSelectableItem(null, getString(R.string.global_spinner_select_option)))
            songManager.getChannels(song).forEach { channel ->
                add(TGSelectableItem(channel, channel.getName()))
            }
        }

    fun createConfigureInstrumentsAction(): TGActionProcessorListener =
        TGActionProcessorListener(findContext(), TGOpenFragmentAction.NAME).apply {
            setAttribute(
                TGOpenFragmentAction.ATTRIBUTE_CONTROLLER,
                TGChannelListFragmentController.getInstance(findContext())
            )
            setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, findActivity())
        }

    fun openConfigureInstruments() {
        createConfigureInstrumentsAction().processOnNewThread()
    }

    fun updateTrackInstrument(track: TGTrack, selectedItem: TGSelectableItem?) {
        val selectedChannel = selectedItem?.getItem() as? TGChannel
        TGActionProcessor(findContext(), TGSetTrackChannelAction.NAME).apply {
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track)
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, selectedChannel)
            processOnNewThread()
        }
    }

    fun getSongManager(): TGSongManager? =
        getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER)

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)

    fun getTrack(): TGTrack? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK)
}

@Composable
fun TGTrackChannelDialogContent(
    channelLabels: List<String>,
    selectedIndex: Int,
    onOpenInstruments: () -> Unit,
    onSave: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    var currentIndex by remember(selectedIndex, channelLabels) {
        mutableIntStateOf(selectedIndex.coerceIn(0, channelLabels.lastIndex))
    }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = stringResource(R.string.channel_edit_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        TGDialogDropdownField(
            label = stringResource(R.string.track_channel_dlg_channel_label),
            selectedText = channelLabels[currentIndex],
            options = channelLabels,
            onOptionSelected = { currentIndex = it },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Start,
        ) {
            OutlinedButton(onClick = onOpenInstruments) {
                Text(stringResource(R.string.action_view_channel_list))
            }
        }
        TGDialogActionButtons(
            onConfirm = { onSave(currentIndex) },
            onCancel = onCancel,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TGTrackChannelDialogContentPreview() {
    GuitarLabTheme {
        TGTrackChannelDialogContent(
            channelLabels = listOf("-- Select --", "Nylon Guitar", "Steel Guitar", "Piano"),
            selectedIndex = 1,
            onOpenInstruments = {},
            onSave = {},
            onCancel = {},
        )
    }
}
