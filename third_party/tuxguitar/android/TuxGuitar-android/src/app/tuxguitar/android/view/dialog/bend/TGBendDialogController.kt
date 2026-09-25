package app.tuxguitar.android.view.dialog.bend

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGBendDialogController : TGComposeBottomSheetDialogController<TGBendDialog>() {
    override fun createNewInstance(): TGBendDialog = TGBendDialog()
}
