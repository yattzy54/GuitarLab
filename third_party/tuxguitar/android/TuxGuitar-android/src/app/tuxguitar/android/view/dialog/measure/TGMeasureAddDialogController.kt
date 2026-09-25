package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGMeasureAddDialogController : TGComposeBottomSheetDialogController<TGMeasureAddDialog>() {
    override fun createNewInstance(): TGMeasureAddDialog = TGMeasureAddDialog()
}
