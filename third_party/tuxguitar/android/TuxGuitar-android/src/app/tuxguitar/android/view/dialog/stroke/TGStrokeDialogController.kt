package app.tuxguitar.android.view.dialog.stroke

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGStrokeDialogController : TGModalFragmentController<TGStrokeDialog>() {
    override fun createNewInstance(): TGStrokeDialog = TGStrokeDialog()
}
