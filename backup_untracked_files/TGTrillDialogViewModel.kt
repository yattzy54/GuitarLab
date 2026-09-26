package app.tuxguitar.android.view.dialog.trill

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGTrillDialogUiState(
    val fret: Int,
    val duration: Int?,
)

class TGTrillDialogViewModel(initial: TGTrillDialogUiState) : EditorStateViewModel<TGTrillDialogUiState>(initial) {
    fun onFretChanged(value: Int) {
        update { it.copy(fret = value) }
    }

    fun onDurationChanged(value: Int?) {
        update { it.copy(duration = value) }
    }
}
