package app.tuxguitar.android.view.dialog.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.android.fragment.TGFragmentTransaction
import app.tuxguitar.android.view.dialog.TGDialogContext
import app.tuxguitar.util.TGContext

abstract class TGDialogFragment : DialogFragment() {
    abstract fun onCreateDialog(): Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (getDialogContext() != null) onCreateDialog() else super.onCreateDialog(savedInstanceState)
    }

    override fun onDestroy() {
        destroyDialogContext()
        super.onDestroy()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (manager.isStateSaved) {
            show(TGFragmentTransaction(manager, true), tag)
        } else {
            super.show(manager, tag)
        }
    }

    fun getDialogContextKey(): String =
        TGDialogContext::class.java.name + "-" + javaClass.name

    fun getDialogContext(): TGDialogContext? =
        findContext().getAttribute(getDialogContextKey())

    fun destroyDialogContext() {
        findContext().removeAttribute(getDialogContextKey())
    }

    fun <T> getAttribute(key: String): T? = getDialogContext()?.getAttribute(key)

    fun findActivity(): TGActivity = TGActivity.requireCurrent()

    fun findContext(): TGContext = TGApplicationUtil.findContext(activity!!)
}
