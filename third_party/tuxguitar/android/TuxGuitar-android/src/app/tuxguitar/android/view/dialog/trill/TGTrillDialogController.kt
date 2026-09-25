package app.tuxguitar.android.view.dialog.trill

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGTrillDialogController : TGModalFragmentController<TGTrillDialog>() {
    override fun createNewInstance(): TGTrillDialog = TGTrillDialog()
}
