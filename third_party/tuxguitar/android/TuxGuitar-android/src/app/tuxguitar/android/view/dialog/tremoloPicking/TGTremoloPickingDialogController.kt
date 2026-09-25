package app.tuxguitar.android.view.dialog.tremoloPicking

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGTremoloPickingDialogController : TGModalFragmentController<TGTremoloPickingDialog>() {
    override fun createNewInstance(): TGTremoloPickingDialog = TGTremoloPickingDialog()
}
