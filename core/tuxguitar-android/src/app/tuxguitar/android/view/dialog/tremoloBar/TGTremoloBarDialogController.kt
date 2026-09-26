package app.tuxguitar.android.view.dialog.tremoloBar

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGTremoloBarDialogController : TGComposeBottomSheetDialogController<TGTremoloBarDialog>() {
    override fun createNewInstance(): TGTremoloBarDialog = TGTremoloBarDialog()
}
