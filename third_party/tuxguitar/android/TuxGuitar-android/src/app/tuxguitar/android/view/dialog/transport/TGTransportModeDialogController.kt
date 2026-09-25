package app.tuxguitar.android.view.dialog.transport

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGTransportModeDialogController : TGComposeBottomSheetDialogController<TGTransportModeDialog>() {
    override fun createNewInstance(): TGTransportModeDialog = TGTransportModeDialog()
}
