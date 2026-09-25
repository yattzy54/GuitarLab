package app.tuxguitar.android.view.dialog.message

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGDialogFragment

class TGMessageDialog : TGDialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(): Dialog {
        val title = getAttribute<String>(TGMessageDialogController.ATTRIBUTE_TITLE)
        val message = getAttribute<String>(TGMessageDialogController.ATTRIBUTE_MESSAGE)
        return AlertDialog.Builder(requireActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.global_button_ok) { dialog, _ -> dialog.dismiss() }
            .create()
    }
}
