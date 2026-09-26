package app.tuxguitar.android.view.dialog.timeSignature

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGTimeSignatureDialogController : TGComposeBottomSheetDialogController<TGTimeSignatureDialog>() {
    override fun createNewInstance(): TGTimeSignatureDialog = TGTimeSignatureDialog()
}
