package app.tuxguitar.android.view.util

import app.tuxguitar.util.TGContext
import app.tuxguitar.util.TGSynchronizer

class TGSyncProcess(private val context: TGContext, private val runnable: Runnable) : TGProcess {

    private var pending = false

    override fun process() {
        if (!this.pending) {
            this.pending = true
            this.processLater()
        }
    }

    private fun processRunnable() {
        this.pending = false
        this.runnable.run()
    }

    private fun processLater() {
        TGSynchronizer.getInstance(this.context).executeLater { this.processRunnable() }
    }
}
