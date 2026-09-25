package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGTrackTuningDialogController : TGComposeBottomSheetDialogController<TGTrackTuningDialog>() {
    override fun createNewInstance(): TGTrackTuningDialog = TGTrackTuningDialog()
}
