package app.tuxguitar.android.synchronizer

import android.os.Handler
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.TGSynchronizer.TGSynchronizerController

class TGSynchronizerControllerImpl(
    private val context: TGContext
) : TGSynchronizerController {
    private val handler = Handler()

    override fun executeLater(target: Runnable) {
        handler.post(TGRunnable(context, target))
    }
}
