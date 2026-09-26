package app.tuxguitar.android.view.processing

import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.TGLock

class TGActionProcessingController(context: TGContext, activity: TGActivity) {
    companion object {
        private const val PROCESSING_DELAY: Long = 100
    }

    private val lock = TGLock(context)
    private val view = TGActionProcessingView(activity)
    private val model = TGActionProcessingModel()
    private var running = false

    fun update(processing: Boolean) {
        try {
            lock.lock()
            if (!isFinished()) {
                model.update(processing)
                if (!running && model.isProcessing()) {
                    running = true
                    start()
                }
            }
        } finally {
            lock.unlock()
        }
    }

    fun process() {
        while (running) {
            if (lock.tryLock()) {
                try {
                    if (!view.isUpdating()) {
                        if (model.isProcessing() && !view.isVisible()) {
                            if (model.getProcessingTime() + PROCESSING_DELAY < System.currentTimeMillis()) {
                                view.postUpdateProgressDialog(true)
                            }
                        }
                        if (!model.isProcessing() && view.isVisible()) {
                            view.postUpdateProgressDialog(false)
                        }
                    }
                    running = model.isProcessing() || view.isUpdating() || view.isVisible()
                } finally {
                    lock.unlock()
                }
            }
            Thread.yield()
        }
    }

    fun finish() {
        try {
            lock.lock()
            running = false
            view.destroy()
        } finally {
            lock.unlock()
        }
    }

    fun start() {
        Thread {
            process()
        }.start()
    }

    fun isFinished(): Boolean = view.isDestroyed()
}
