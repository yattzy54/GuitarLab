package app.tuxguitar.android.view.dialog.tremoloPicking

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGTremoloPickingDialogController : TGComposeBottomSheetDialogController<TGTremoloPickingDialog>() {
    override fun createNewInstance(): TGTremoloPickingDialog = TGTremoloPickingDialog()
}
