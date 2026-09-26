package app.tuxguitar.android.view.dialog.keySignature

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGKeySignatureDialogUiState(
    val keySignature: Int,
    val applyToEnd: Boolean,
)

class TGKeySignatureDialogViewModel(initial: TGKeySignatureDialogUiState) : EditorStateViewModel<TGKeySignatureDialogUiState>(initial) {
    fun onKeySignatureChanged(value: Int) {
        update { it.copy(keySignature = value) }
    }

    fun onApplyToEndChanged(value: Boolean) {
        update { it.copy(applyToEnd = value) }
    }
}
