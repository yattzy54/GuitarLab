package app.tuxguitar.android.view.dialog.tempo

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGTempoDialogController : TGComposeBottomSheetDialogController<TGTempoDialog>() {
    override fun createNewInstance(): TGTempoDialog = TGTempoDialog()
}
