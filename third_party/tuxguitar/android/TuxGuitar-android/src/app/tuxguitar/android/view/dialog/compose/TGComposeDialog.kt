package app.tuxguitar.android.view.dialog.compose

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.android.view.dialog.TGDialogContext
import app.tuxguitar.util.TGContext

/**
 * Base class for dialogs rendered as a Material3 [androidx.compose.material3.ModalBottomSheet]
 * directly inside the Compose tree that hosts [TGActivity] (see
 * `TuxGuitarScreen.kt`'s dialog host), replacing the previous
 * `DialogFragment`-based implementation. Instances are plain objects created
 * per [TGComposeBottomSheetDialogController.showDialog] call; there is no
 * fragment-manager persistence to worry about since a dialog is recreated
 * from its [TGDialogContext] every time it is shown.
 */
abstract class TGComposeDialog {
    var dialogContext: TGDialogContext? = null

    @Composable
    abstract fun SheetContent(onDismiss: () -> Unit)

    fun getDialogContextKey(): String =
        TGDialogContext::class.java.name + "-" + javaClass.name

    fun <T> getAttribute(key: String): T? = dialogContext?.getAttribute(key)

    fun findActivity(): TGActivity = TGActivity.requireCurrent()

    fun findContext(): TGContext = TGApplicationUtil.findContext(findActivity())

    fun getString(resId: Int): String = findActivity().getString(resId)
    fun getString(resId: Int, vararg formatArgs: Any?): String = findActivity().getString(resId, *formatArgs)


    /**
     * Runs [runnable] on the UI thread. Unlike the old Fragment-based
     * `postWhenReady`, this no longer needs to wait for a View to exist: the
     * sheet's content is plain Compose state driven from [dialogContext], so
     * posting straight to the main thread is enough for the next
     * recomposition to pick it up.
     */
    fun postWhenReady(runnable: Runnable) {
        Handler(Looper.getMainLooper()).post(runnable)
    }

    /** Called once this dialog becomes [TGActivity.currentDialog] (replaces `DialogFragment.onResume`). */
    open fun onShow() {}

    /** Called once this dialog stops being [TGActivity.currentDialog] (replaces `DialogFragment.onPause`). */
    open fun onHide() {}
}
