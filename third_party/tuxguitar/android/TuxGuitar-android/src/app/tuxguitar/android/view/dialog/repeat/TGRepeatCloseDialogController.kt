package app.tuxguitar.android.view.dialog.repeat

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGRepeatCloseDialogController : TGModalFragmentController<TGRepeatCloseDialog>() {
    override fun createNewInstance(): TGRepeatCloseDialog = TGRepeatCloseDialog()
}
