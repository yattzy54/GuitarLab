package app.tuxguitar.android.view.dialog.fragment

import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.android.view.dialog.TGDialogContext
import app.tuxguitar.android.view.dialog.TGDialogController

abstract class TGDialogFragmentController<T : TGDialogFragment> : TGDialogController {
    abstract fun createNewInstance(): T

    override fun showDialog(activity: TGActivity, dialogContext: TGDialogContext) {
        val dialog = createNewInstance()
        val context = TGApplicationUtil.findContext(activity)
        context.setAttribute(dialog.getDialogContextKey(), dialogContext)
        dialog.show(activity.supportFragmentManager, "NoticeDialogFragment")
    }
}
