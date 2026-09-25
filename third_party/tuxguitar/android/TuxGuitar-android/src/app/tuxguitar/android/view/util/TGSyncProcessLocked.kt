package app.tuxguitar.android.view.util

import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.TGException
import app.tuxguitar.util.TGLock
import app.tuxguitar.util.TGSynchronizer

class TGSyncProcessLocked(private val context: TGContext, private val runnable: Runnable) : TGProcess {

    private var pending = false

    override fun process() {
        if (!this.pending) {
            this.pending = true
            this.processLaterLocked()
        }
    }

    private fun processRunnable() {
        this.pending = false
        this.runnable.run()
    }

    private fun processLaterLocked() {
        TGSynchronizer.getInstance(this.context).executeLater {
            val lock = this.findLockControl()
            if (lock.tryLock()) {
                try {
                    this.processRunnable()
                } finally {
                    lock.unlock()
                }
            } else {
                this.processLaterLocked()
            }
        }
    }

    private fun findLockControl(): TGLock = TGEditorManager.getInstance(this.context).lockControl
}
