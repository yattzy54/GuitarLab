package app.tuxguitar.android.ui.editor

import android.content.Intent
import android.view.KeyEvent
import android.view.MenuItem
import androidx.compose.runtime.Composable
import app.tuxguitar.android.domain.model.EditorMode
import app.tuxguitar.android.domain.model.EditorState

/**
 * Android-only bridge. Activity/View references stay here, never in a ViewModel
 * or the domain repository contract.
 */
interface EditorHost {
    @Composable
    fun Content(
        sessionId: String,
        mode: EditorMode,
        state: EditorState,
        onDismissDialog: (Long) -> Unit,
    )

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    fun onNewIntent(intent: Intent)
    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean
    fun onOptionsItemSelected(item: MenuItem): Boolean
}
