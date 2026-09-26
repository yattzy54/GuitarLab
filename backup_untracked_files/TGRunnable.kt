package app.tuxguitar.android.synchronizer

import app.tuxguitar.util.TGContext
import app.tuxguitar.util.TGException
import app.tuxguitar.util.error.TGErrorManager

class TGRunnable(
    private val context: TGContext,
    private val runnable: Runnable,
) : Runnable {
    override fun run() {
        try {
            runnable.run()
        } catch (throwable: Throwable) {
            TGErrorManager.getInstance(context).handleError(TGException(throwable))
        }
    }
}
