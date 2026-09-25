package app.tuxguitar.android.view.dialog.trill

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGTrillDialogController : TGComposeBottomSheetDialogController<TGTrillDialog>() {
    override fun createNewInstance(): TGTrillDialog = TGTrillDialog()
}
