package app.tuxguitar.android.view.dialog.grace

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGGraceDialogController : TGModalFragmentController<TGGraceDialog>() {
    override fun createNewInstance(): TGGraceDialog = TGGraceDialog()
}
