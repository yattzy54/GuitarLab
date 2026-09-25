package app.tuxguitar.android.view.dialog.transport

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGTransportModeDialogController : TGModalFragmentController<TGTransportModeDialog>() {
    override fun createNewInstance(): TGTransportModeDialog = TGTransportModeDialog()
}
