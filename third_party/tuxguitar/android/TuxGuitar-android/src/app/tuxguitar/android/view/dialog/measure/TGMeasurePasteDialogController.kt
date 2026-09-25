package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGMeasurePasteDialogController : TGComposeBottomSheetDialogController<TGMeasurePasteDialog>() {
    override fun createNewInstance(): TGMeasurePasteDialog = TGMeasurePasteDialog()
}
