package app.tuxguitar.android.view.dialog.channel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.android.view.dialog.compose.TGDialogDropdownField
import app.tuxguitar.android.view.dialog.compose.TGDialogSliderField
import app.tuxguitar.android.view.dialog.compose.TGDropdownOption
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction
import app.tuxguitar.editor.event.TGUpdateEvent
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.player.base.MidiInstrument
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGChannel
import app.tuxguitar.song.models.TGSong

data class TGChannelEditFields(
    val name: String,
    val program: Short,
    val bank: Short,
    val percussion: Boolean,
    val percussionEnabled: Boolean,
    val bankEnabled: Boolean,
    val volume: Int,
    val balance: Int,
    val reverb: Int,
    val chorus: Int,
    val phaser: Int,
    val tremolo: Int,
)

class TGChannelEditDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val instrumentPrograms = remember { createInstrumentProgramOptions() }
        val percussionPrograms = remember { createPercussionProgramOptions() }
        val bankOptions = remember { createBankOptions() }
        var fields by remember { mutableStateOf(createFields()) }

        fun refresh() {
            fields = createFields()
        }

        fun processNameIfNeeded(name: String) {
            if (name != getChannel().getName()) {
                createUpdateChannelAction(name).process()
                refresh()
            }
        }

        DisposableEffect(Unit) {
            val listener = object : TGEventListener {
                override fun processEvent(event: TGEvent) {
                    if (TGUpdateEvent.EVENT_TYPE == event.eventType) {
                        val type = event.getAttribute<Int>(TGUpdateEvent.PROPERTY_UPDATE_MODE)
                        if (type == TGUpdateEvent.SELECTION) {
                            findActivity().requireActivity().runOnUiThread { refresh() }
                        }
                    }
                }
            }
            TGEditorManager.getInstance(findContext()).addUpdateListener(listener)
            onDispose {
                TGEditorManager.getInstance(findContext()).removeUpdateListener(listener)
                processNameIfNeeded(fields.name)
            }
        }

        val programOptions = if (fields.percussion) percussionPrograms else instrumentPrograms
        TGChannelEditDialogContent(
            state = fields,
            programOptions = programOptions,
            bankOptions = bankOptions,
            onNameChange = { fields = fields.copy(name = it) },
            onNameCommit = { name -> processNameIfNeeded(name) },
            onProgramChange = { program ->
                fields = fields.copy(program = program)
                createUpdateAttributeAction(fields.name, TGUpdateChannelAction.ATTRIBUTE_PROGRAM, program).process()
                refresh()
            },
            onBankChange = { bank ->
                fields = fields.copy(bank = bank)
                if (!fields.percussion) {
                    createUpdateAttributeAction(fields.name, TGUpdateChannelAction.ATTRIBUTE_BANK, bank).process()
                    refresh()
                }
            },
            onPercussionChange = { percussion ->
                fields = fields.copy(percussion = percussion)
                createUpdatePercussionAction(fields.name, percussion).process()
                refresh()
            },
            onVolumeChange = { value ->
                if (value != fields.volume) {
                    fields = fields.copy(volume = value)
                    createUpdateAttributeAction(fields.name, TGUpdateChannelAction.ATTRIBUTE_VOLUME, value.toShort()).process()
                    refresh()
                }
            },
            onBalanceChange = { value ->
                if (value != fields.balance) {
                    fields = fields.copy(balance = value)
                    createUpdateAttributeAction(fields.name, TGUpdateChannelAction.ATTRIBUTE_BALANCE, value.toShort()).process()
                    refresh()
                }
            },
            onReverbChange = { value ->
                if (value != fields.reverb) {
                    fields = fields.copy(reverb = value)
                    createUpdateAttributeAction(fields.name, TGUpdateChannelAction.ATTRIBUTE_REVERB, value.toShort()).process()
                    refresh()
                }
            },
            onChorusChange = { value ->
                if (value != fields.chorus) {
                    fields = fields.copy(chorus = value)
                    createUpdateAttributeAction(fields.name, TGUpdateChannelAction.ATTRIBUTE_CHORUS, value.toShort()).process()
                    refresh()
                }
            },
            onPhaserChange = { value ->
                if (value != fields.phaser) {
                    fields = fields.copy(phaser = value)
                    createUpdateAttributeAction(fields.name, TGUpdateChannelAction.ATTRIBUTE_PHASER, value.toShort()).process()
                    refresh()
                }
            },
            onTremoloChange = { value ->
                if (value != fields.tremolo) {
                    fields = fields.copy(tremolo = value)
                    createUpdateAttributeAction(fields.name, TGUpdateChannelAction.ATTRIBUTE_TREMOLO, value.toShort()).process()
                    refresh()
                }
            },
            onDismiss = {
                processNameIfNeeded(fields.name)
                onDismiss()
            },
        )
    }

    private fun createFields(): TGChannelEditFields {
        val songManager = requireNotNull(getAttribute<TGSongManager>(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER))
        val song = requireNotNull(getAttribute<TGSong>(TGDocumentContextAttributes.ATTRIBUTE_SONG))
        val channel = getChannel()
        val percussionChannel = channel.isPercussionChannel()
        val anyPercussionChannel = songManager.isAnyPercussionChannel(song)
        val anyTrackConnectedToChannel = songManager.isAnyTrackConnectedToChannel(song, channel.getChannelId())
        return TGChannelEditFields(
            name = channel.getName(),
            program = channel.getProgram(),
            bank = channel.getBank(),
            percussion = percussionChannel,
            percussionEnabled = !anyTrackConnectedToChannel && (!anyPercussionChannel || percussionChannel),
            bankEnabled = !percussionChannel,
            volume = channel.getVolume().toInt(),
            balance = channel.getBalance().toInt(),
            reverb = channel.getReverb().toInt(),
            chorus = channel.getChorus().toInt(),
            phaser = channel.getPhaser().toInt(),
            tremolo = channel.getTremolo().toInt(),
        )
    }

    private fun createUnnamedPrograms(): List<TGDropdownOption<Short>> =
        (0 until 128).map { value ->
            val shortValue = value.toShort()
            TGDropdownOption(shortValue, findActivity().getString(R.string.channel_edit_dlg_program_value, shortValue))
        }

    private fun createInstrumentProgramOptions(): List<TGDropdownOption<Short>> {
        val instruments: Array<MidiInstrument>? = MidiPlayer.getInstance(findContext()).instruments
        if (instruments == null) {
            return createUnnamedPrograms()
        }
        return instruments.take(128).mapIndexed { index, instrument ->
            TGDropdownOption(index.toShort(), instrument.getName())
        }
    }

    private fun createPercussionProgramOptions(): List<TGDropdownOption<Short>> = createUnnamedPrograms()

    private fun createBankOptions(): List<TGDropdownOption<Short>> =
        (0 until 128).map { value ->
            val shortValue = value.toShort()
            TGDropdownOption(shortValue, findActivity().getString(R.string.channel_edit_dlg_bank_value, shortValue))
        }

    private fun createUpdateChannelAction(name: String): TGActionProcessor =
        TGActionProcessor(findContext(), TGUpdateChannelAction.NAME).apply {
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, getChannel())
            setAttribute(TGUpdateChannelAction.ATTRIBUTE_NAME, name)
        }

    private fun createUpdateAttributeAction(name: String, attributeName: String, attributeValue: Any?): TGActionProcessor =
        createUpdateChannelAction(name).apply { setAttribute(attributeName, attributeValue) }

    private fun createUpdatePercussionAction(name: String, percussion: Boolean): TGActionProcessor {
        val bank = if (percussion) TGChannel.DEFAULT_PERCUSSION_BANK else TGChannel.DEFAULT_BANK
        val program = if (percussion) TGChannel.DEFAULT_PERCUSSION_PROGRAM else TGChannel.DEFAULT_PROGRAM
        return createUpdateChannelAction(name).apply {
            setAttribute(TGUpdateChannelAction.ATTRIBUTE_BANK, bank)
            setAttribute(TGUpdateChannelAction.ATTRIBUTE_PROGRAM, program)
        }
    }

    fun getChannel(): TGChannel =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL))
}

@Composable
fun TGChannelEditDialogContent(
    state: TGChannelEditFields,
    programOptions: List<TGDropdownOption<Short>>,
    bankOptions: List<TGDropdownOption<Short>>,
    onNameChange: (String) -> Unit,
    onNameCommit: (String) -> Unit,
    onProgramChange: (Short) -> Unit,
    onBankChange: (Short) -> Unit,
    onPercussionChange: (Boolean) -> Unit,
    onVolumeChange: (Int) -> Unit,
    onBalanceChange: (Int) -> Unit,
    onReverbChange: (Int) -> Unit,
    onChorusChange: (Int) -> Unit,
    onPhaserChange: (Int) -> Unit,
    onTremoloChange: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 600.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.channel_edit_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.channel_edit_dlg_name_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (!it.isFocused) {
                        onNameCommit(state.name)
                    }
                },
        )
        TGDialogDropdownField(
            label = stringResource(R.string.channel_edit_dlg_program_label),
            selectedOption = programOptions.firstOrNull { it.value == state.program } ?: programOptions.first(),
            options = programOptions,
            onSelected = { onProgramChange(it.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        )
        TGDialogDropdownField(
            label = stringResource(R.string.channel_edit_dlg_bank_label),
            selectedOption = bankOptions.first { it.value == state.bank },
            options = bankOptions,
            onSelected = { onBankChange(it.value) },
            enabled = state.bankEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = state.percussion,
                enabled = state.percussionEnabled,
                onCheckedChange = onPercussionChange,
            )
            Text(text = stringResource(R.string.channel_edit_dlg_percussion_label))
        }

        TGDialogSliderField(
            label = stringResource(R.string.channel_edit_dlg_volume_label),
            value = state.volume,
            onValueChange = onVolumeChange,
            modifier = Modifier.padding(top = 16.dp),
        )
        TGDialogSliderField(
            label = stringResource(R.string.channel_edit_dlg_balance_label),
            value = state.balance,
            onValueChange = onBalanceChange,
            modifier = Modifier.padding(top = 12.dp),
        )
        TGDialogSliderField(
            label = stringResource(R.string.channel_edit_dlg_reverb_label),
            value = state.reverb,
            onValueChange = onReverbChange,
            modifier = Modifier.padding(top = 12.dp),
        )
        TGDialogSliderField(
            label = stringResource(R.string.channel_edit_dlg_chorus_label),
            value = state.chorus,
            onValueChange = onChorusChange,
            modifier = Modifier.padding(top = 12.dp),
        )
        TGDialogSliderField(
            label = stringResource(R.string.channel_edit_dlg_phaser_label),
            value = state.phaser,
            onValueChange = onPhaserChange,
            modifier = Modifier.padding(top = 12.dp),
        )
        TGDialogSliderField(
            label = stringResource(R.string.channel_edit_dlg_tremolo_label),
            value = state.tremolo,
            onValueChange = onTremoloChange,
            modifier = Modifier.padding(top = 12.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGChannelEditDialogPreview() {
    MaterialTheme {
        TGChannelEditDialogContent(
            state = TGChannelEditFields(
                name = "Clean Guitar",
                program = 27,
                bank = 0,
                percussion = false,
                percussionEnabled = true,
                bankEnabled = true,
                volume = 110,
                balance = 64,
                reverb = 20,
                chorus = 12,
                phaser = 0,
                tremolo = 0,
            ),
            programOptions = listOf(
                TGDropdownOption(27, "Electric Guitar (clean)"),
                TGDropdownOption(28, "Electric Guitar (muted)"),
            ),
            bankOptions = listOf(
                TGDropdownOption(0, "Bank #0"),
                TGDropdownOption(1, "Bank #1"),
            ),
            onNameChange = {},
            onNameCommit = {},
            onProgramChange = {},
            onBankChange = {},
            onPercussionChange = {},
            onVolumeChange = {},
            onBalanceChange = {},
            onReverbChange = {},
            onChorusChange = {},
            onPhaserChange = {},
            onTremoloChange = {},
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TGChannelEditDialogPercussionPreview() {
    MaterialTheme {
        TGChannelEditDialogContent(
            state = TGChannelEditFields(
                name = "Percussion",
                program = 0,
                bank = TGChannel.DEFAULT_PERCUSSION_BANK,
                percussion = true,
                percussionEnabled = true,
                bankEnabled = false,
                volume = 127,
                balance = 64,
                reverb = 0,
                chorus = 0,
                phaser = 0,
                tremolo = 0,
            ),
            programOptions = listOf(
                TGDropdownOption(0, "Instrument #0"),
                TGDropdownOption(1, "Instrument #1"),
            ),
            bankOptions = listOf(TGDropdownOption(TGChannel.DEFAULT_PERCUSSION_BANK, "Bank #128")),
            onNameChange = {},
            onNameCommit = {},
            onProgramChange = {},
            onBankChange = {},
            onPercussionChange = {},
            onVolumeChange = {},
            onBalanceChange = {},
            onReverbChange = {},
            onChorusChange = {},
            onPhaserChange = {},
            onTremoloChange = {},
            onDismiss = {},
        )
    }
}
