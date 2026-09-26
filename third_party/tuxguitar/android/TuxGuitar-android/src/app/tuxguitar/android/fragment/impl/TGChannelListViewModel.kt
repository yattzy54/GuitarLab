package app.tuxguitar.android.fragment.impl

import app.tuxguitar.android.ui.state.EditorStateViewModel
import app.tuxguitar.song.models.TGChannel

data class TGChannelListItemState(
    val channel: TGChannel,
    val name: String,
    val volume: Short,
    val removable: Boolean,
)

class TGChannelListViewModel : EditorStateViewModel<List<TGChannelListItemState>>(emptyList()) {
    fun updateChannels(channels: List<TGChannel>, isRemovable: (TGChannel) -> Boolean) {
        val rows = channels.map { TGChannelListItemState(it, it.name, it.volume, isRemovable(it)) }
        update { rows }
    }

    fun changeVolume(channel: TGChannel, volume: Short, onChange: () -> Unit) {
        if (volume != channel.volume && volume in 0..127) onChange()
    }
}
