package app.tuxguitar.android.view.dialog.keySignature

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGKeySignatureDialogController : TGModalFragmentController<TGKeySignatureDialog>() {
    override fun createNewInstance(): TGKeySignatureDialog = TGKeySignatureDialog()
}
