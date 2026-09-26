package app.tuxguitar.android.view.dialog.grace

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGGraceFields(
    val fret: Int,
    val deadNote: Boolean,
    val onBeat: Boolean,
    val duration: Int,
    val dynamic: Int,
    val transition: Int,
)

class TGGraceDialogViewModel(initial: TGGraceFields) : EditorStateViewModel<TGGraceFields>(initial) {
    fun onFretChanged(value: Int) {
        update { it.copy(fret = value) }
    }

    fun onDeadNoteChanged(value: Boolean) {
        update { it.copy(deadNote = value) }
    }

    fun onOnBeatChanged(value: Boolean) {
        update { it.copy(onBeat = value) }
    }

    fun onDurationChanged(value: Int) {
        update { it.copy(duration = value) }
    }

    fun onDynamicChanged(value: Int) {
        update { it.copy(dynamic = value) }
    }

    fun onTransitionChanged(value: Int) {
        update { it.copy(transition = value) }
    }
}
