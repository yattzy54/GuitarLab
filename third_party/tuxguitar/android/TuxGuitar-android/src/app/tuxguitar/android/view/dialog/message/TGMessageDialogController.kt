package app.tuxguitar.android.view.dialog.message

import app.tuxguitar.android.view.dialog.fragment.TGDialogFragmentController

class TGMessageDialogController : TGDialogFragmentController<TGMessageDialog>() {
    override fun createNewInstance(): TGMessageDialog = TGMessageDialog()

    companion object {
        const val ATTRIBUTE_TITLE = "title"
        const val ATTRIBUTE_MESSAGE = "message"
    }
}
