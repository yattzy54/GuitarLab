package app.tuxguitar.android.view.dialog.clef

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGClefDialogUiState(
    val clef: Int,
    val applyToEnd: Boolean,
)

class TGClefDialogViewModel(initial: TGClefDialogUiState) : EditorStateViewModel<TGClefDialogUiState>(initial) {
    fun onClefChanged(value: Int) {
        update { it.copy(clef = value) }
    }

    fun onApplyToEndChanged(value: Boolean) {
        update { it.copy(applyToEnd = value) }
    }
}
