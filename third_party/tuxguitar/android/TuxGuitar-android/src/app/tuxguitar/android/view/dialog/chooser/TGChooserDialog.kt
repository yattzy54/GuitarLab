package app.tuxguitar.android.view.dialog.chooser

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import app.tuxguitar.android.view.dialog.fragment.TGDialogFragment

class TGChooserDialog<T> : TGDialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(): Dialog {
        val title = getAttribute<String>(TGChooserDialogController.ATTRIBUTE_TITLE)
        val handler = requireNotNull(
            getAttribute<TGChooserDialogHandler<T>>(TGChooserDialogController.ATTRIBUTE_HANDLER)
        )
        val options = requireNotNull(
            getAttribute<List<TGChooserDialogOption<T>>>(TGChooserDialogController.ATTRIBUTE_OPTIONS)
        )
        val items = options.map { it.label }.toTypedArray()

        return AlertDialog.Builder(requireActivity())
            .setTitle(title)
            .setItems(items) { dialog, which ->
                if (which in options.indices) {
                    onChooseInNewThread(handler, options[which].value)
                }
            }
            .create()
    }

    fun onChooseInNewThread(handler: TGChooserDialogHandler<T>, value: T?) {
        Thread { handler.onChoose(value) }.start()
    }
}
