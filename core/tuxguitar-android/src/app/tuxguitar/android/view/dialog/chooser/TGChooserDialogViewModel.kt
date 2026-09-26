package app.tuxguitar.android.view.dialog.chooser

import app.tuxguitar.android.ui.state.EditorStateViewModel

class TGChooserDialogViewModel : EditorStateViewModel<Int?>(null) {
    fun choose(index: Int, optionCount: Int, onChoose: () -> Unit) {
        require(index in 0 until optionCount) { "Unknown chooser option: $index" }
        if (state.value != null) return
        update { index }
        onChoose()
    }
}
