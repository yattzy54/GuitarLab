package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGMeasureCopyDialogController : TGComposeBottomSheetDialogController<TGMeasureCopyDialog>() {
    override fun createNewInstance(): TGMeasureCopyDialog = TGMeasureCopyDialog()
}
