package app.tuxguitar.android.view.dialog.pickstroke

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGPickStrokeDialogState(
    val direction: Int,
)

class TGPickStrokeDialogViewModel(initial: TGPickStrokeDialogState) : EditorStateViewModel<TGPickStrokeDialogState>(initial) {
    fun onDirectionChanged(value: Int) {
        update { it.copy(direction = value) }
    }
}
