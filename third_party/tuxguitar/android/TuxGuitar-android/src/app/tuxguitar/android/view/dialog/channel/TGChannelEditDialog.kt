package app.tuxguitar.android.view.dialog.channel

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.player.base.MidiInstrument
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGChannel
import app.tuxguitar.song.models.TGSong

class TGChannelEditDialog : TGModalFragment(R.layout.view_channel_edit_dialog) {
    private var eventListener: TGEventListener? = null
    private lateinit var instrumentPrograms: ArrayAdapter<TGSelectableItem>
    private lateinit var percussionPrograms: ArrayAdapter<TGSelectableItem>

    fun getChannel(): TGChannel =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL))

    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(false, false, R.string.channel_edit_dlg_title)
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        eventListener = TGChannelEditEventListener(this)
        fillProgramAdapters()
        fillBanks()
        updateItems()
    }

    override fun onShowView() {
        appendListeners()
    }

    override fun onHideView() {
        removeListeners()
    }

    fun updateItems() {
        updateStates()
        fillPrograms()
        fillNameValue()
        fillBankValue()
        fillProgramValue()
        fillPercussionValue()
        fillVolumeValue()
        fillBalanceValue()
        fillReverbValue()
        fillChorusValue()
        fillPhaserValue()
        fillTremoloValue()
    }

    fun updateStates() {
        val songManager = requireNotNull(
            getAttribute<TGSongManager>(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER)
        )
        val song = requireNotNull(getAttribute<TGSong>(TGDocumentContextAttributes.ATTRIBUTE_SONG))
        val channel = getChannel()
        val percussionChannel = channel.isPercussionChannel()
        val anyPercussionChannel = songManager.isAnyPercussionChannel(song)
        val anyTrackConnectedToChannel =
            songManager.isAnyTrackConnectedToChannel(song, channel.getChannelId())
        setViewEnabled(
            R.id.channel_edit_dlg_percussion_value,
            !anyTrackConnectedToChannel && (!anyPercussionChannel || percussionChannel)
        )
        setViewEnabled(R.id.channel_edit_dlg_bank_value, !percussionChannel)
    }

    fun appendListeners() {
        requireView().findViewById<View>(R.id.channel_edit_dlg_name_value)
            .onFocusChangeListener = createNameFocusChangeListener()
        requireView().findViewById<Spinner>(R.id.channel_edit_dlg_bank_value)
            .onItemSelectedListener = createBankSelectedListener()
        requireView().findViewById<Spinner>(R.id.channel_edit_dlg_program_value)
            .onItemSelectedListener = createProgramSelectedListener()
        requireView().findViewById<CheckBox>(R.id.channel_edit_dlg_percussion_value)
            .setOnCheckedChangeListener(createPercussionChangeListener())
        requireView().findViewById<SeekBar>(R.id.channel_edit_dlg_volume_value)
            .setOnSeekBarChangeListener(createVolumeChangeListener())
        requireView().findViewById<SeekBar>(R.id.channel_edit_dlg_balance_value)
            .setOnSeekBarChangeListener(createBalanceChangeListener())
        requireView().findViewById<SeekBar>(R.id.channel_edit_dlg_reverb_value)
            .setOnSeekBarChangeListener(createReverbChangeListener())
        requireView().findViewById<SeekBar>(R.id.channel_edit_dlg_chorus_value)
            .setOnSeekBarChangeListener(createChorusChangeListener())
        requireView().findViewById<SeekBar>(R.id.channel_edit_dlg_phaser_value)
            .setOnSeekBarChangeListener(createPhaserChangeListener())
        requireView().findViewById<SeekBar>(R.id.channel_edit_dlg_tremolo_value)
            .setOnSeekBarChangeListener(createTremoloChangeListener())
        TGEditorManager.getInstance(findContext())
            .addUpdateListener(requireNotNull(eventListener))
    }

    fun removeListeners() {
        eventListener?.let { TGEditorManager.getInstance(findContext()).removeUpdateListener(it) }
    }

    fun createUnamedPrograms(): List<TGSelectableItem> =
        (0 until 128).map { value ->
            val shortValue = value.toShort()
            TGSelectableItem(
                shortValue,
                getString(R.string.channel_edit_dlg_program_value, shortValue)
            )
        }

    fun createInstrumentPrograms(): List<TGSelectableItem> {
        val instruments: Array<MidiInstrument>? = MidiPlayer.getInstance(findContext()).instruments
        if (instruments == null) return createUnamedPrograms()
        return instruments.take(128).mapIndexed { index, instrument ->
            TGSelectableItem(index.toShort(), instrument.getName())
        }
    }

    fun fillProgramAdapters() {
        instrumentPrograms = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createInstrumentPrograms()
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        percussionPrograms = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createUnamedPrograms()
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    fun fillPrograms() {
        updateSpinnerAdapter(
            R.id.channel_edit_dlg_program_value,
            if (getChannel().isPercussionChannel()) percussionPrograms else instrumentPrograms
        )
    }

    fun fillProgramValue() {
        updateSpinnerValue(R.id.channel_edit_dlg_program_value, getChannel().getProgram().toShort())
    }

    fun findSelectedProgram(): Short =
        (getSpinnerValue(R.id.channel_edit_dlg_program_value)?.getItem() as Number).toShort()

    fun createBankValues(): List<TGSelectableItem> =
        (0 until 128).map { value ->
            val shortValue = value.toShort()
            TGSelectableItem(shortValue, getString(R.string.channel_edit_dlg_bank_value, shortValue))
        }

    fun fillBanks() {
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createBankValues()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        updateSpinnerAdapter(R.id.channel_edit_dlg_bank_value, adapter)
    }

    fun fillBankValue() {
        updateSpinnerValue(R.id.channel_edit_dlg_bank_value, getChannel().getBank().toShort())
    }

    fun findSelectedBank(): Short =
        (getSpinnerValue(R.id.channel_edit_dlg_bank_value)?.getItem() as Number).toShort()

    fun fillNameValue() {
        setTextFieldValue(R.id.channel_edit_dlg_name_value, getChannel().getName())
    }

    fun findNameValue(): String = getTextFieldValue(R.id.channel_edit_dlg_name_value)

    fun fillPercussionValue() {
        setCheckBoxValue(R.id.channel_edit_dlg_percussion_value, getChannel().isPercussionChannel())
    }

    fun findPercussionValue(): Boolean =
        getCheckBoxValue(R.id.channel_edit_dlg_percussion_value)

    fun fillVolumeValue() {
        setSeekBarValue(R.id.channel_edit_dlg_volume_value, getChannel().getVolume().toInt())
    }

    fun fillBalanceValue() {
        setSeekBarValue(R.id.channel_edit_dlg_balance_value, getChannel().getBalance().toInt())
    }

    fun fillReverbValue() {
        setSeekBarValue(R.id.channel_edit_dlg_reverb_value, getChannel().getReverb().toInt())
    }

    fun fillChorusValue() {
        setSeekBarValue(R.id.channel_edit_dlg_chorus_value, getChannel().getChorus().toInt())
    }

    fun fillPhaserValue() {
        setSeekBarValue(R.id.channel_edit_dlg_phaser_value, getChannel().getPhaser().toInt())
    }

    fun fillTremoloValue() {
        setSeekBarValue(R.id.channel_edit_dlg_tremolo_value, getChannel().getTremolo().toInt())
    }

    fun setViewEnabled(id: Int, enabled: Boolean) {
        requireView().findViewById<View>(id).isEnabled = enabled
    }

    fun updateSpinnerValue(id: Int, value: Any?) {
        setSpinnerValue(id, TGSelectableItem(value, null))
    }

    fun updateSpinnerAdapter(id: Int, adapter: ArrayAdapter<TGSelectableItem>) {
        val spinner = requireView().findViewById<Spinner>(id)
        if (!isSameValue(adapter, spinner.adapter)) spinner.adapter = adapter
    }

    fun setTextFieldValue(id: Int, value: String) {
        if (!isSameValue(value, getTextFieldValue(id))) {
            requireView().findViewById<EditText>(id).setText(value)
        }
    }

    fun getTextFieldValue(id: Int): String =
        requireView().findViewById<EditText>(id).text.toString()

    fun setCheckBoxValue(id: Int, value: Boolean?) {
        if (!isSameValue(value, getCheckBoxValue(id))) {
            requireView().findViewById<CheckBox>(id).isChecked =
                requireNotNull(value) { "Checkbox value cannot be null" }
        }
    }

    fun getCheckBoxValue(id: Int): Boolean =
        requireView().findViewById<CheckBox>(id).isChecked

    fun getSpinnerValue(id: Int): TGSelectableItem? =
        requireView().findViewById<Spinner>(id).selectedItem as? TGSelectableItem

    fun setSpinnerValue(id: Int, selectedItem: TGSelectableItem?) {
        if (!isSameValue(selectedItem, getSpinnerValue(id))) {
            val spinner = requireView().findViewById<Spinner>(id)
            @Suppress("UNCHECKED_CAST")
            spinner.setSelection(
                (spinner.adapter as ArrayAdapter<TGSelectableItem>).getPosition(selectedItem),
                false
            )
        }
    }

    fun getSeekBarValue(id: Int): Int =
        requireView().findViewById<SeekBar>(id).progress

    fun setSeekBarValue(id: Int, value: Int?) {
        if (!isSameValue(value, getSeekBarValue(id))) {
            requireView().findViewById<SeekBar>(id).progress =
                requireNotNull(value) { "Seek bar value cannot be null" }
        }
    }

    fun isSameValue(v1: Any?, v2: Any?): Boolean = v1 === v2 || (v1 != null && v1 == v2)

    fun isChannelNameUpdated(): Boolean = findNameValue() != getChannel().getName()

    fun createUpdateChannelAction(): TGActionProcessor =
        TGActionProcessor(findContext(), TGUpdateChannelAction.NAME).apply {
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, getChannel())
            setAttribute(TGUpdateChannelAction.ATTRIBUTE_NAME, findNameValue())
        }

    fun createUpdateAttributteAction(attributeName: String, attributeValue: Any?): TGActionProcessor =
        createUpdateChannelAction().apply { setAttribute(attributeName, attributeValue) }

    fun createUpdateBankAction(): TGActionProcessor =
        createUpdateAttributteAction(TGUpdateChannelAction.ATTRIBUTE_BANK, findSelectedBank())

    fun createUpdateProgramAction(): TGActionProcessor =
        createUpdateAttributteAction(TGUpdateChannelAction.ATTRIBUTE_PROGRAM, findSelectedProgram())

    fun createUpdatePercussionAction(): TGActionProcessor {
        val percussion = findPercussionValue()
        val bank = if (percussion) TGChannel.DEFAULT_PERCUSSION_BANK else TGChannel.DEFAULT_BANK
        val program =
            if (percussion) TGChannel.DEFAULT_PERCUSSION_PROGRAM else TGChannel.DEFAULT_PROGRAM
        return createUpdateChannelAction().apply {
            setAttribute(TGUpdateChannelAction.ATTRIBUTE_BANK, bank)
            setAttribute(TGUpdateChannelAction.ATTRIBUTE_PROGRAM, program)
        }
    }

    fun createNameFocusChangeListener(): View.OnFocusChangeListener =
        View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && isChannelNameUpdated()) createUpdateChannelAction().process()
        }

    fun createProgramSelectedListener(): AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                createUpdateProgramAction().process()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                createUpdateProgramAction().process()
            }
        }

    fun createBankSelectedListener(): AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!getChannel().isPercussionChannel()) createUpdateBankAction().process()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (!getChannel().isPercussionChannel()) createUpdateBankAction().process()
            }
        }

    fun createPercussionChangeListener(): CompoundButton.OnCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { _, _ -> createUpdatePercussionAction().process() }

    private fun createVolumeChangeListener(): SeekBar.OnSeekBarChangeListener =
        createShortLevelChangeListener(TGUpdateChannelAction.ATTRIBUTE_VOLUME)

    private fun createBalanceChangeListener(): SeekBar.OnSeekBarChangeListener =
        createShortLevelChangeListener(TGUpdateChannelAction.ATTRIBUTE_BALANCE)

    private fun createReverbChangeListener(): SeekBar.OnSeekBarChangeListener =
        createShortLevelChangeListener(TGUpdateChannelAction.ATTRIBUTE_REVERB)

    private fun createChorusChangeListener(): SeekBar.OnSeekBarChangeListener =
        createShortLevelChangeListener(TGUpdateChannelAction.ATTRIBUTE_CHORUS)

    private fun createPhaserChangeListener(): SeekBar.OnSeekBarChangeListener =
        createShortLevelChangeListener(TGUpdateChannelAction.ATTRIBUTE_PHASER)

    private fun createTremoloChangeListener(): SeekBar.OnSeekBarChangeListener =
        createShortLevelChangeListener(TGUpdateChannelAction.ATTRIBUTE_TREMOLO)

    private fun createShortLevelChangeListener(attribute: String): SeekBar.OnSeekBarChangeListener =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress in 0..127) {
                    createUpdateAttributteAction(attribute, progress.toShort()).process()
                }
            }
        }
}
