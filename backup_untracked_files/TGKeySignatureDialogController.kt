package app.tuxguitar.android.view.dialog.keySignature

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGKeySignatureDialogController : TGComposeBottomSheetDialogController<TGKeySignatureDialog>() {
    override fun createNewInstance(): TGKeySignatureDialog = TGKeySignatureDialog()
}
