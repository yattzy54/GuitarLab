package app.tuxguitar.android.view.dialog.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.android.fragment.TGFragmentTransaction
import app.tuxguitar.android.view.dialog.TGDialogContext
import app.tuxguitar.util.TGContext

/**
 * Base class for dialogs rendered as a Material3 [ModalBottomSheet] instead of
 * a classic [android.app.AlertDialog].
 *
 * Subclasses only need to provide the [SheetContent] composable; window setup,
 * the dialog attribute plumbing ([TGDialogContext]) and dismissal wiring are
 * handled here so existing [app.tuxguitar.android.view.dialog.TGDialogController]
 * call sites keep working unchanged.
 */
abstract class TGComposeBottomSheetDialogFragment : DialogFragment() {

    @Composable
    abstract fun SheetContent(onDismiss: () -> Unit)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    ModalBottomSheet(
                        onDismissRequest = { dismissAllowingStateLoss() },
                        sheetState = sheetState,
                        modifier = Modifier.systemBarsPadding(),
                    ) {
                        SheetContent(onDismiss = { dismissAllowingStateLoss() })
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.transparent)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
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

    fun findActivity(): TGActivity = activity as TGActivity

    fun findContext(): TGContext = TGApplicationUtil.findContext(activity!!)
}
