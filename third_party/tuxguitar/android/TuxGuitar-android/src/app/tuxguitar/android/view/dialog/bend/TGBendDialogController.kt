package app.tuxguitar.android.view.dialog.bend

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGBendDialogController : TGModalFragmentController<TGBendDialog>() {
    override fun createNewInstance(): TGBendDialog = TGBendDialog()
}
