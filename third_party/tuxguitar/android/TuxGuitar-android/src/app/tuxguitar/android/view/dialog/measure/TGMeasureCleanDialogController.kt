package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGMeasureCleanDialogController : TGModalFragmentController<TGMeasureCleanDialog>() {
    override fun createNewInstance(): TGMeasureCleanDialog = TGMeasureCleanDialog()
}
