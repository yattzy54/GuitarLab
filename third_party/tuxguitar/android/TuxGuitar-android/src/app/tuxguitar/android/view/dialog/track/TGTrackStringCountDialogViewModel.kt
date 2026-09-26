package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGTrackStringCountDialogState(
    val selectedIndex: Int,
)

class TGTrackStringCountDialogViewModel(initial: TGTrackStringCountDialogState) : EditorStateViewModel<TGTrackStringCountDialogState>(initial) {
    fun onSelectedIndexChanged(value: Int) {
        update { it.copy(selectedIndex = value) }
    }
}
