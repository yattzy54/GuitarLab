package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGMeasurePasteDialogState(
    val count: Int,
    val mode: Int,
)

class TGMeasurePasteDialogViewModel(initial: TGMeasurePasteDialogState) : EditorStateViewModel<TGMeasurePasteDialogState>(initial) {
    fun onCountChanged(value: Int) {
        update { it.copy(count = value) }
    }

    fun onModeChanged(value: Int) {
        update { it.copy(mode = value) }
    }
}
