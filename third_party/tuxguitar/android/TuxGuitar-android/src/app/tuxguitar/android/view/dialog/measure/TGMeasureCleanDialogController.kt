package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGMeasureCleanDialogController : TGComposeBottomSheetDialogController<TGMeasureCleanDialog>() {
    override fun createNewInstance(): TGMeasureCleanDialog = TGMeasureCleanDialog()
}
