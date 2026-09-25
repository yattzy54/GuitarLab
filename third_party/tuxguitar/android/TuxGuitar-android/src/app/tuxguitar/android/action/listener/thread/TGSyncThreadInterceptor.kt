package app.tuxguitar.android.action.listener.thread

import app.tuxguitar.action.*
import app.tuxguitar.util.TGContext

class TGSyncThreadInterceptor(context: TGContext) : TGSyncThreadAction(context), TGActionInterceptor {
 private val actionIds = mutableListOf<String>()
 fun containsActionId(id: String)=actionIds.contains(id); fun addActionId(id:String)=actionIds.add(id); fun removeActionId(id:String)=actionIds.remove(id)
 override fun intercept(id: String, context: TGActionContext): Boolean { if (containsActionId(id) && !isUiThread()) { runInUiThread(id,context); return true }; return false }
}
