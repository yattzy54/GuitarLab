package app.tuxguitar.android.view.dialog.tremoloPicking

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGTremoloPickingDialogState(
    val selectedIndex: Int,
)

class TGTremoloPickingDialogViewModel(initial: TGTremoloPickingDialogState) : EditorStateViewModel<TGTremoloPickingDialogState>(initial) {
    fun onSelectedIndexChanged(value: Int) {
        update { it.copy(selectedIndex = value) }
    }
}
