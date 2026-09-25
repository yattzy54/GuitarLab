package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGMeasureCopyDialogController : TGModalFragmentController<TGMeasureCopyDialog>() {
    override fun createNewInstance(): TGMeasureCopyDialog = TGMeasureCopyDialog()
}
