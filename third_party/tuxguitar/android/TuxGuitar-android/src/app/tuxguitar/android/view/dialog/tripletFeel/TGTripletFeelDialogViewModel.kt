package app.tuxguitar.android.view.dialog.tripletFeel

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGTripletFeelDialogUiState(
    val tripletFeel: Int,
    val applyToEnd: Boolean,
)

class TGTripletFeelDialogViewModel(initial: TGTripletFeelDialogUiState) : EditorStateViewModel<TGTripletFeelDialogUiState>(initial) {
    fun onTripletFeelChanged(value: Int) {
        update { it.copy(tripletFeel = value) }
    }

    fun onApplyToEndChanged(value: Boolean) {
        update { it.copy(applyToEnd = value) }
    }
}
