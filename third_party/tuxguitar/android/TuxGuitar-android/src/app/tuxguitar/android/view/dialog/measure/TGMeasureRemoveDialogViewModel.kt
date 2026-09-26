package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGMeasureRemoveDialogState(
    val fromMeasure: Int,
    val toMeasure: Int,
)

class TGMeasureRemoveDialogViewModel(initial: TGMeasureRemoveDialogState) : EditorStateViewModel<TGMeasureRemoveDialogState>(initial) {
    fun onFromMeasureChanged(value: Int) {
        update { it.copy(fromMeasure = value) }
    }

    fun onToMeasureChanged(value: Int) {
        update { it.copy(toMeasure = value) }
    }
}
