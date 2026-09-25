package app.tuxguitar.android.activity

import android.os.Bundle
import android.view.ContextMenu
import android.view.KeyEvent
import android.view.View
import app.tuxguitar.android.R

/**
 * Read-only TuxGuitar activity.
 *
 * Kept as a Kotlin subclass so the activity layer can move independently from
 * the legacy Java editor engine.
 */
class TGReaderActivity : TGActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TGMainFragment now hosts view_main.xml through a Compose AndroidView, whose
        // content is inflated on the next layout pass rather than synchronously here.
        // Posting the visibility change keeps this working regardless of that timing.
        findViewById<View>(R.id.root_layout)?.post {
            findViewById<View>(R.id.main_bottom)?.visibility = View.GONE
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        view: View,
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
