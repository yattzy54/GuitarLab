package app.tuxguitar.android.view.dialog

import app.tuxguitar.android.activity.TGActivity

interface TGDialogController {
    fun showDialog(activity: TGActivity, dialogContext: TGDialogContext)
}
