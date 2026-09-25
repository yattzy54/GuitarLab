package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGMeasureRemoveDialogController : TGModalFragmentController<TGMeasureRemoveDialog>() {
    override fun createNewInstance(): TGMeasureRemoveDialog = TGMeasureRemoveDialog()
}
