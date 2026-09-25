package app.tuxguitar.android.view.dialog.measure

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGMeasureAddDialogController : TGModalFragmentController<TGMeasureAddDialog>() {
    override fun createNewInstance(): TGMeasureAddDialog = TGMeasureAddDialog()
}
