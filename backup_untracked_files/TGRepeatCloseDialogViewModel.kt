package app.tuxguitar.android.view.dialog.repeat

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGRepeatCloseDialogState(
    val repeatCount: Int,
)

class TGRepeatCloseDialogViewModel(initial: TGRepeatCloseDialogState) : EditorStateViewModel<TGRepeatCloseDialogState>(initial) {
    fun onRepeatCountChanged(value: Int) {
        update { it.copy(repeatCount = value) }
    }
}
