package app.tuxguitar.android.view.dialog.stroke

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGStrokeDialogUiState(
    val direction: Int,
    val duration: Int,
)

class TGStrokeDialogViewModel(initial: TGStrokeDialogUiState) : EditorStateViewModel<TGStrokeDialogUiState>(initial) {
    fun onDirectionChanged(value: Int) {
        update { it.copy(direction = value) }
    }

    fun onDurationChanged(value: Int) {
        update { it.copy(duration = value) }
    }
}
