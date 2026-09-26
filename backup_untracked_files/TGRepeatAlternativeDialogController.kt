package app.tuxguitar.android.view.dialog.repeat

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGRepeatAlternativeDialogController : TGComposeBottomSheetDialogController<TGRepeatAlternativeDialog>() {
    override fun createNewInstance(): TGRepeatAlternativeDialog = TGRepeatAlternativeDialog()
}
