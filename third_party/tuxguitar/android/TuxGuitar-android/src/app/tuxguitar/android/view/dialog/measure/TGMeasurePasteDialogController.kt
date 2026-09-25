package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGMeasurePasteDialogController : TGModalFragmentController<TGMeasurePasteDialog>() {
    override fun createNewInstance(): TGMeasurePasteDialog = TGMeasurePasteDialog()
}
