package app.tuxguitar.android.view.dialog.pickstroke

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGPickStrokeDialogController : TGComposeBottomSheetDialogController<TGPickStrokeDialog>() {
    override fun createNewInstance(): TGPickStrokeDialog = TGPickStrokeDialog()
}
