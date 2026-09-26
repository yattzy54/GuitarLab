package app.tuxguitar.android.action.listener.thread

import android.os.Looper
import app.tuxguitar.action.*
import app.tuxguitar.util.*

abstract class TGSyncThreadAction(private val context: TGContext) {
 fun getContext() = context
 fun isUiThread() = Looper.myLooper() == Looper.getMainLooper()
 fun runInUiThread(id: String, actionContext: TGActionContext) { TGSynchronizer.getInstance(context).executeLater { executeInterceptedAction(id, actionContext) } }
 open fun executeInterceptedAction(actionId: String, actionContext: TGActionContext) { try { TGActionManager.getInstance(context).execute(actionId, actionContext) } catch (e: TGActionException) { e.printStackTrace() } }
}
