package app.tuxguitar.android.view.dialog.repeat

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGRepeatCloseDialogController : TGComposeBottomSheetDialogController<TGRepeatCloseDialog>() {
    override fun createNewInstance(): TGRepeatCloseDialog = TGRepeatCloseDialog()
}
