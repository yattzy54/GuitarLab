package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGMeasureCopyDialogState(
    val fromMeasure: Int,
    val toMeasure: Int,
    val allTracks: Boolean,
)

class TGMeasureCopyDialogViewModel(initial: TGMeasureCopyDialogState) : EditorStateViewModel<TGMeasureCopyDialogState>(initial) {
    fun onFromMeasureChanged(value: Int) {
        update { it.copy(fromMeasure = value) }
    }

    fun onToMeasureChanged(value: Int) {
        update { it.copy(toMeasure = value) }
    }

    fun onAllTracksChanged(value: Boolean) {
        update { it.copy(allTracks = value) }
    }
}
