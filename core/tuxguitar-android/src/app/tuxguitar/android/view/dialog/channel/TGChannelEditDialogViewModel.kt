package app.tuxguitar.android.view.dialog.channel

import app.tuxguitar.android.ui.state.EditorStateViewModel

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

class TGChannelEditDialogViewModel(initial: TGChannelEditFields) : EditorStateViewModel<TGChannelEditFields>(initial) {
    fun onNameChanged(value: String) {
        update { it.copy(name = value) }
    }

    fun onProgramChanged(value: Short) {
        update { it.copy(program = value) }
    }

    fun onBankChanged(value: Short) {
        update { it.copy(bank = value) }
    }

    fun onPercussionChanged(value: Boolean) {
        update { it.copy(percussion = value) }
    }

    fun onPercussionEnabledChanged(value: Boolean) {
        update { it.copy(percussionEnabled = value) }
    }

    fun onBankEnabledChanged(value: Boolean) {
        update { it.copy(bankEnabled = value) }
    }

    fun onVolumeChanged(value: Int) {
        update { it.copy(volume = value) }
    }

    fun onBalanceChanged(value: Int) {
        update { it.copy(balance = value) }
    }

    fun onReverbChanged(value: Int) {
        update { it.copy(reverb = value) }
    }

    fun onChorusChanged(value: Int) {
        update { it.copy(chorus = value) }
    }

    fun onPhaserChanged(value: Int) {
        update { it.copy(phaser = value) }
    }

    fun onTremoloChanged(value: Int) {
        update { it.copy(tremolo = value) }
    }

    fun onFieldsChanged(value: TGChannelEditFields) {
        update { value }
    }
}
