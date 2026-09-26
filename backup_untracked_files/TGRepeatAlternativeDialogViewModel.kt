package app.tuxguitar.android.view.dialog.repeat

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGRepeatAlternativeDialogState(
    val selectedEndings: Int,
)

class TGRepeatAlternativeDialogViewModel(initial: TGRepeatAlternativeDialogState) : EditorStateViewModel<TGRepeatAlternativeDialogState>(initial) {
    fun onSelectedEndingsChanged(value: Int) {
        update { it.copy(selectedEndings = value) }
    }
}
