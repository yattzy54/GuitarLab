package app.tuxguitar.android.view.dialog.fragment

import app.tuxguitar.android.fragment.TGCachedFragment
import app.tuxguitar.android.view.dialog.TGDialogContext

abstract class TGModalFragment(layout: Int) : TGCachedFragment(layout) {
    fun getDialogContextKey(): String =
        TGDialogContext::class.java.name + "-" + javaClass.name

    fun getDialogContext(): TGDialogContext? =
        findContext().getAttribute(getDialogContextKey())

    fun destroyDialogContext() {
        findContext().removeAttribute(getDialogContextKey())
    }

    fun <T> getAttribute(key: String): T? = getDialogContext()?.getAttribute(key)

    fun close() {
        findActivity().callBackAction()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyDialogContext()
    }
}
