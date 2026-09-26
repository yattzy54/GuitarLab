package app.tuxguitar.android.view.dialog.timeSignature

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGTimeSignatureDialogUiState(
    val numerator: Int,
    val denominator: Int,
    val applyToEnd: Boolean,
)

class TGTimeSignatureDialogViewModel(initial: TGTimeSignatureDialogUiState) : EditorStateViewModel<TGTimeSignatureDialogUiState>(initial) {
    fun onNumeratorChanged(value: Int) {
        update { it.copy(numerator = value) }
    }

    fun onDenominatorChanged(value: Int) {
        update { it.copy(denominator = value) }
    }

    fun onApplyToEndChanged(value: Boolean) {
        update { it.copy(applyToEnd = value) }
    }
}
