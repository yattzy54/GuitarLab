package app.tuxguitar.android.view.dialog.tempo

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGTempoDialogState(
    val tempoIndex: Int,
    val baseIndex: Int,
    val applyIndex: Int,
)

class TGTempoDialogViewModel(initial: TGTempoDialogState) : EditorStateViewModel<TGTempoDialogState>(initial) {
    fun onTempoIndexChanged(value: Int) {
        update { it.copy(tempoIndex = value) }
    }

    fun onBaseIndexChanged(value: Int) {
        update { it.copy(baseIndex = value) }
    }

    fun onApplyIndexChanged(value: Int) {
        update { it.copy(applyIndex = value) }
    }
}
