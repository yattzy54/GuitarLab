package app.tuxguitar.android.view.dialog.message

import app.tuxguitar.android.ui.state.EditorStateViewModel

class TGMessageDialogViewModel : EditorStateViewModel<Boolean>(false) {
    fun acknowledge(onDismiss: () -> Unit) {
        if (state.value) return
        update { true }
        onDismiss()
    }
}
