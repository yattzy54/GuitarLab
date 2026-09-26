package app.tuxguitar.android.view.dialog.clef

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGClefDialogController : TGComposeBottomSheetDialogController<TGClefDialog>() {
    override fun createNewInstance(): TGClefDialog = TGClefDialog()
}
