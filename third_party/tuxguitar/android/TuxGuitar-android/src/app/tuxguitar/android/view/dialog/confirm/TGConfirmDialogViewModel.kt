package app.tuxguitar.android.view.dialog.confirm

import app.tuxguitar.android.ui.state.EditorStateViewModel

enum class TGConfirmationDecision { CONFIRM, CANCEL }

class TGConfirmDialogViewModel : EditorStateViewModel<TGConfirmationDecision?>(null) {
    fun decide(decision: TGConfirmationDecision, onDecision: () -> Unit) {
        if (state.value != null) return
        update { decision }
        onDecision()
    }
}
