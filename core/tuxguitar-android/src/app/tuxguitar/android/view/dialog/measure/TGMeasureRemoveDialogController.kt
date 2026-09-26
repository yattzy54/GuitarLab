package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGMeasureRemoveDialogController : TGComposeBottomSheetDialogController<TGMeasureRemoveDialog>() {
    override fun createNewInstance(): TGMeasureRemoveDialog = TGMeasureRemoveDialog()
}
