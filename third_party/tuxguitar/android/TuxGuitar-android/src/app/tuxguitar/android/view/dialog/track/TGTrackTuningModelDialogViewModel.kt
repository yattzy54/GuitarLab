package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGTrackTuningModelDialogState(
    val currentIndex: Int,
)

class TGTrackTuningModelDialogViewModel(initial: TGTrackTuningModelDialogState) : EditorStateViewModel<TGTrackTuningModelDialogState>(initial) {
    fun onCurrentIndexChanged(value: Int) {
        update { it.copy(currentIndex = value) }
    }
}
