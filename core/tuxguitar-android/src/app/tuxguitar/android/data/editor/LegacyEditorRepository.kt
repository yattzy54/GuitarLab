package app.tuxguitar.android.data.editor

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGReaderActivity
import app.tuxguitar.android.domain.model.EditorDestination
import app.tuxguitar.android.domain.model.EditorMode
import app.tuxguitar.android.domain.model.EditorState
import app.tuxguitar.android.domain.repository.EditorRepository
import app.tuxguitar.android.fragment.impl.TGBrowserFragment
import app.tuxguitar.android.fragment.impl.TGChannelListFragment
import app.tuxguitar.android.fragment.impl.TGMainFragment
import app.tuxguitar.android.fragment.impl.TGPreferencesFragment
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class LegacyEditorRepository @Inject constructor() : EditorRepository {
    private val mutableState = MutableStateFlow(EditorState())
    override val state = mutableState.asStateFlow()

    internal var engine: TGActivity? = null
        private set
    private var rootView: View? = null
    private var dialog: TGComposeDialog? = null
    private var nextDialogId = 0L

    internal fun attach(
        host: AppCompatActivity,
        sessionId: String,
        mode: EditorMode,
        compositionContext: CompositionContext,
        content: @Composable (TGActivity) -> Unit,
    ): View {
        // NavHost transitions may briefly compose two destinations. Only the
        // latest owns the legacy singleton; a stale onRelease cannot close it.
        release()
        val current = if (mode == EditorMode.READ_ONLY) TGReaderActivity() else TGActivity()
        engine = current
        mutableState.value = EditorState(sessionId = sessionId, mode = mode)
        current.onUiStateChanged = {
            host.runOnUiThread {
                if (engine === current) publishState(current)
            }
        }
        current.onFinishRequested = {
            host.runOnUiThread {
                if (engine === current) {
                    mutableState.value = mutableState.value.copy(exitRequested = true)
                }
            }
        }
        try {
            val view = current.getOrCreateRootView(host, compositionContext) { content(current) }
            rootView = view
            publishState(current)
            return view
        } catch (failure: Exception) {
            try {
                release()
            } catch (cleanupFailure: Exception) {
                failure.addSuppressed(cleanupFailure)
            }
            throw failure
        }
    }

    internal fun detach(view: View) {
        if (rootView === view) release()
    }

    private fun release() {
        val previous = engine
        engine = null
        rootView = null
        dialog = null
        mutableState.value = EditorState()
        previous?.destroyRootView()
    }

    private fun publishState(current: TGActivity) {
        val currentDialog = current.currentDialog
        val dialogId = if (currentDialog == null) {
            null
        } else if (currentDialog === dialog) {
            state.value.dialogId
        } else {
            ++nextDialogId
        }
        dialog = currentDialog
        val destination = when (val screen = current.getNavigationManager().currentScreen) {
            null -> null
            is TGMainFragment -> EditorDestination.SCORE
            is TGBrowserFragment -> EditorDestination.BROWSER
            is TGChannelListFragment -> EditorDestination.CHANNELS
            is TGPreferencesFragment -> EditorDestination.PREFERENCES
            else -> error("Unsupported editor screen: ${screen.javaClass.name}")
        }
        mutableState.value = state.value.copy(
            isAttached = rootView != null,
            destination = destination,
            dialogId = dialogId,
            revision = state.value.revision + 1,
        )
    }

    override fun goBack(sessionId: String) {
        if (state.value.sessionId != sessionId) return
        val current = engine ?: return
        val dialogId = state.value.dialogId
        when {
            dialogId != null -> dismissDialog(sessionId, dialogId)
            current.getDrawerManager().isOpen() -> current.getDrawerManager().closeDrawer()
            else -> current.callBackAction()
        }
    }

    override fun dismissDialog(sessionId: String, dialogId: Long) {
        if (state.value.sessionId != sessionId || state.value.dialogId != dialogId) return
        val current = engine ?: return
        current.currentDialog?.let(current::dismissComposeDialog)
    }

    override fun consumeExitRequest(sessionId: String) {
        if (state.value.sessionId == sessionId) {
            mutableState.value = state.value.copy(exitRequested = false)
        }
    }
}
