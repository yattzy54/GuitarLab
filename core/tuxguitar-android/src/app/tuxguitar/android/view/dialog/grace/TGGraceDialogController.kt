package app.tuxguitar.android.view.dialog.grace

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGGraceDialogController : TGComposeBottomSheetDialogController<TGGraceDialog>() {
    override fun createNewInstance(): TGGraceDialog = TGGraceDialog()
}
