package app.tuxguitar.android.view.dialog.repeat

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGRepeatAlternativeDialogController : TGModalFragmentController<TGRepeatAlternativeDialog>() {
    override fun createNewInstance(): TGRepeatAlternativeDialog = TGRepeatAlternativeDialog()
}
