package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGMeasureCleanDialogState(
    val fromMeasure: Int,
    val toMeasure: Int,
)

class TGMeasureCleanDialogViewModel(initial: TGMeasureCleanDialogState) : EditorStateViewModel<TGMeasureCleanDialogState>(initial) {
    fun onFromMeasureChanged(value: Int) {
        update { it.copy(fromMeasure = value) }
    }

    fun onToMeasureChanged(value: Int) {
        update { it.copy(toMeasure = value) }
    }
}
