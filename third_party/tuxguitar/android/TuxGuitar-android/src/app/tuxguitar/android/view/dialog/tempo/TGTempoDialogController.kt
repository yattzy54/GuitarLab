package app.tuxguitar.android.view.dialog.tempo

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGTempoDialogController : TGModalFragmentController<TGTempoDialog>() {
    override fun createNewInstance(): TGTempoDialog = TGTempoDialog()
}
