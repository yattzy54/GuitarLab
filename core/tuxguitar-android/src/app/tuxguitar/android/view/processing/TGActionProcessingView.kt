package app.tuxguitar.android.view.processing

import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.util.TGException
import app.tuxguitar.util.TGSynchronizer

class TGActionProcessingView(private val activity: TGActivity) {
    private var dialog: TGProcessingDialog? = null
    private var updating = false
    private var destroyed = false

    private fun createProgressDialog() {
        if (!isDestroyed() && !isVisible()) {
            dialog = TGProcessingDialog(activity).apply {
                show()
            }
        }
    }

    private fun dismissProgressDialog() {
        if (!isDestroyed() && isVisible()) {
            dialog?.dismiss()
            dialog = null
        }
    }

    private fun updateProgressDialog(visible: Boolean) {
        if (visible) {
            createProgressDialog()
        } else {
            dismissProgressDialog()
        }
    }

    fun postUpdateProgressDialog(visible: Boolean) {
        if (!isDestroyed()) {
            updating = true
            TGSynchronizer.getInstance(activity.findContext()).executeLater(object : Runnable {
                @Throws(TGException::class)
                override fun run() {
                    updateProgressDialog(visible)
                    updating = false
                }
            })
        }
    }

    fun destroy() {
        dismissProgressDialog()
        destroyed = true
    }

    fun isVisible(): Boolean = dialog != null

    fun isUpdating(): Boolean = updating

    fun isDestroyed(): Boolean = destroyed || activity.isFragmentDestroyed()
}
