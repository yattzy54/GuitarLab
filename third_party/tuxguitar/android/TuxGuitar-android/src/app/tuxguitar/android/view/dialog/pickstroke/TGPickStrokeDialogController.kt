package app.tuxguitar.android.view.dialog.pickstroke

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGPickStrokeDialogController : TGModalFragmentController<TGPickStrokeDialog>() {
    override fun createNewInstance(): TGPickStrokeDialog = TGPickStrokeDialog()
}
