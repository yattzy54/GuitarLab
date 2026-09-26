package app.tuxguitar.android.view.dialog.text

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGTextDialogState(
    val text: String,
)

class TGTextDialogViewModel(initial: TGTextDialogState) : EditorStateViewModel<TGTextDialogState>(initial) {
    fun onTextChanged(value: String) {
        update { it.copy(text = value) }
    }
}
