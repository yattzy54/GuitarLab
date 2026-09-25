package app.tuxguitar.android.view.dialog.timeSignature

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGTimeSignatureDialogController : TGModalFragmentController<TGTimeSignatureDialog>() {
    override fun createNewInstance(): TGTimeSignatureDialog = TGTimeSignatureDialog()
}
