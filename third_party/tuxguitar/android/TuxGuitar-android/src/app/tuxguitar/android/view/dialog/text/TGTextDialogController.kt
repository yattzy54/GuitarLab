package app.tuxguitar.android.view.dialog.text

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGTextDialogController : TGModalFragmentController<TGTextDialog>() {
    override fun createNewInstance(): TGTextDialog = TGTextDialog()
}
