package app.tuxguitar.android.view.dialog.tremoloBar

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGTremoloBarDialogController : TGModalFragmentController<TGTremoloBarDialog>() {
    override fun createNewInstance(): TGTremoloBarDialog = TGTremoloBarDialog()
}
