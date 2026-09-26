package app.tuxguitar.android.view.dialog.stroke

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGStrokeDialogController : TGComposeBottomSheetDialogController<TGStrokeDialog>() {
    override fun createNewInstance(): TGStrokeDialog = TGStrokeDialog()
}
