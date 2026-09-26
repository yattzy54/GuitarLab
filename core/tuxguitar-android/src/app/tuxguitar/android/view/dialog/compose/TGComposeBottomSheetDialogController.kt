package app.tuxguitar.android.view.dialog.compose

import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.view.dialog.TGDialogContext
import app.tuxguitar.android.view.dialog.TGDialogController

/**
 * [TGDialogController] variant for dialogs backed by [TGComposeDialog].
 */
abstract class TGComposeBottomSheetDialogController<T : TGComposeDialog> :
    TGDialogController {
    abstract fun createNewInstance(): T

    override fun showDialog(activity: TGActivity, dialogContext: TGDialogContext) {
        val dialog = createNewInstance()
        dialog.dialogContext = dialogContext
        activity.showComposeDialog(dialog)
    }
}
