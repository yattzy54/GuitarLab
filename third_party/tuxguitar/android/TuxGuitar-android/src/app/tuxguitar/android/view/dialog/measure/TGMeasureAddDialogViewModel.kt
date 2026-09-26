package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGMeasureAddDialogState(
    val count: Int,
    val measureNumber: Int,
)

class TGMeasureAddDialogViewModel(initial: TGMeasureAddDialogState) : EditorStateViewModel<TGMeasureAddDialogState>(initial) {
    fun onCountChanged(value: Int) {
        update { it.copy(count = value) }
    }

    fun onMeasureNumberChanged(value: Int) {
        update { it.copy(measureNumber = value) }
    }
}
