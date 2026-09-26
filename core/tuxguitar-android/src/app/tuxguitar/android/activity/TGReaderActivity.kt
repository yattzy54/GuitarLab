package app.tuxguitar.android.activity

import android.view.ContextMenu
import android.view.KeyEvent
import android.view.View

/**
 * Read-only TuxGuitar activity.
 *
 * Kept as a Kotlin subclass so the activity layer can move independently from
 * the legacy Java editor engine.
 */
class TGReaderActivity : TGActivity() {
    override val isReadOnly: Boolean = true

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?,
    ) {
        // The reader intentionally has no note or measure editing menu.
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DEL,
            KeyEvent.KEYCODE_TAB,
            KeyEvent.KEYCODE_MINUS,
            KeyEvent.KEYCODE_EQUALS,
            KeyEvent.KEYCODE_PERIOD,
            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> true
            else -> super.onKeyDown(keyCode, event)
        }
    }

    companion object {
        const val NAME = "app.tuxguitar.android.activity.TGReaderActivity"
    }
}
