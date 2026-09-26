package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGTrackNameDialogState(
    val name: String,
)

class TGTrackNameDialogViewModel(initial: TGTrackNameDialogState) : EditorStateViewModel<TGTrackNameDialogState>(initial) {
    fun onNameChanged(value: String) {
        update { it.copy(name = value) }
    }
}
