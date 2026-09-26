package app.tuxguitar.android.view.dialog.message

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGMessageDialogController : TGComposeBottomSheetDialogController<TGMessageDialog>() {
    override fun createNewInstance(): TGMessageDialog = TGMessageDialog()

    companion object {
        const val ATTRIBUTE_TITLE = "title"
        const val ATTRIBUTE_MESSAGE = "message"
    }
}
