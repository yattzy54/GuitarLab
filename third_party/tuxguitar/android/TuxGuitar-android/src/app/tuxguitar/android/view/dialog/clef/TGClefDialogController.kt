package app.tuxguitar.android.view.dialog.clef

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGClefDialogController : TGModalFragmentController<TGClefDialog>() {
    override fun createNewInstance(): TGClefDialog = TGClefDialog()
}
