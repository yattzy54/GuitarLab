package app.tuxguitar.android.ui.editor

import android.content.Intent
import android.view.KeyEvent
import android.view.MenuItem
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import app.tuxguitar.android.data.editor.LegacyEditorRepository
import app.tuxguitar.android.domain.model.EditorMode
import app.tuxguitar.android.domain.model.EditorState
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import javax.inject.Inject
import javax.inject.Singleton

/** Keeps the legacy View/Canvas and dialog renderers behind the Android host API. */
@Singleton
class LegacyEditorHost @Inject constructor(
    private val repository: LegacyEditorRepository,
) : EditorHost {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(
        sessionId: String,
        mode: EditorMode,
        state: EditorState,
        onDismissDialog: (Long) -> Unit,
    ) {
        val activity = requireNotNull(LocalActivity.current as? AppCompatActivity) {
            "TuxGuitar must be hosted by an AppCompatActivity"
        }
        val compositionContext = rememberCompositionContext()
        val currentState = rememberUpdatedState(state)
        val currentOnDismiss by rememberUpdatedState(onDismissDialog)
        key(activity, sessionId, mode) {
            AndroidView(
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
                factory = {
                    repository.attach(activity, sessionId, mode, compositionContext) { engine ->
                        val uiState = currentState.value
                        GuitarLabTheme {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background,
                            ) {
                                if (uiState.isAttached && repository.engine === engine) {
                                    key(uiState.destination) {
                                        engine.getNavigationManager().currentScreen?.Content()
                                    }
                                }
                            }
                            uiState.dialogId?.let { dialogId ->
                                if (repository.engine === engine) {
                                    engine.currentDialog?.let { dialog ->
                                        key(dialogId) {
                                            ModalBottomSheet(
                                                onDismissRequest = { currentOnDismiss(dialogId) },
                                                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                                                modifier = Modifier.systemBarsPadding(),
                                            ) {
                                                CompositionLocalProvider(LocalViewModelStoreOwner provides dialog) {
                                                    dialog.SheetContent { currentOnDismiss(dialogId) }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                onRelease = repository::detach,
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        repository.engine?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        repository.engine?.onRequestPermissionsResult(requestCode, permissions.toList().toTypedArray(), grantResults)
    }

    override fun onNewIntent(intent: Intent) {
        repository.engine?.onNewIntent(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean =
        repository.engine?.onKeyDown(keyCode, event) ?: false

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        repository.engine?.onOptionsItemSelected(item) ?: false
}
