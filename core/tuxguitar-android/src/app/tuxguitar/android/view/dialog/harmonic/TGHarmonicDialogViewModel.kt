package app.tuxguitar.android.view.dialog.harmonic

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGHarmonicFields(
    val type: Int,
    val data: Int,
    val naturalAvailable: Boolean,
)

class TGHarmonicDialogViewModel(initial: TGHarmonicFields) : EditorStateViewModel<TGHarmonicFields>(initial) {
    fun onTypeChanged(value: Int) {
        update { it.copy(type = value) }
    }

    fun onDataChanged(value: Int) {
        update { it.copy(data = value) }
    }
}
