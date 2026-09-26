package app.tuxguitar.android.view.dialog.text

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGTextDialogController : TGComposeBottomSheetDialogController<TGTextDialog>() {
    override fun createNewInstance(): TGTextDialog = TGTextDialog()
}
