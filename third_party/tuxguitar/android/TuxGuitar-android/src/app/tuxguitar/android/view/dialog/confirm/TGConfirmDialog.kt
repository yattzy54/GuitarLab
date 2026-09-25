package app.tuxguitar.android.view.dialog.confirm

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGDialogFragment

class TGConfirmDialog : TGDialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setTitle(R.string.confirm_dlg_title)
            .setMessage(getMessage())
            .setPositiveButton(R.string.global_button_ok) { dialog, _ ->
                onSuccess()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.global_button_cancel) { dialog, _ ->
                onCancel()
                dialog.dismiss()
            }
            .create()
    }

    fun onSuccess() {
        getRunnable()?.run()
    }

    fun onCancel() {
        getCancelRunnable()?.run()
    }

    fun getMessage(): String? = getAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE)

    fun getRunnable(): Runnable? = getAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE)

    fun getCancelRunnable(): Runnable? =
        getAttribute(TGConfirmDialogController.ATTRIBUTE_CANCEL_RUNNABLE)
}
