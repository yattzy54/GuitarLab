package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGTrackChannelDialogState(
    val currentIndex: Int,
)

class TGTrackChannelDialogViewModel(initial: TGTrackChannelDialogState) : EditorStateViewModel<TGTrackChannelDialogState>(initial) {
    fun onCurrentIndexChanged(value: Int) {
        update { it.copy(currentIndex = value) }
    }
}
