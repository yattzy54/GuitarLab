package app.tuxguitar.android.view.dialog.confirm

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGConfirmDialogController : TGComposeBottomSheetDialogController<TGConfirmDialog>() {
    override fun createNewInstance(): TGConfirmDialog = TGConfirmDialog()

    companion object {
        const val ATTRIBUTE_MESSAGE = "message"
        const val ATTRIBUTE_RUNNABLE = "runnable"
        const val ATTRIBUTE_CANCEL_RUNNABLE = "cancelRunnable"
    }
}
