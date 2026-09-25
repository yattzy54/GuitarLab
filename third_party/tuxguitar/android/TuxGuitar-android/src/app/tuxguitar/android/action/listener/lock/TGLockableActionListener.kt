package app.tuxguitar.android.action.listener.lock

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionErrorEvent
import app.tuxguitar.action.TGActionEvent
import app.tuxguitar.action.TGActionInterceptor
import app.tuxguitar.action.TGActionPostExecutionEvent
import app.tuxguitar.action.TGActionPreExecutionEvent
import app.tuxguitar.android.action.listener.thread.TGSyncThreadAction
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.util.TGContext

class TGLockableActionListener(context: TGContext) :
    TGSyncThreadAction(context), TGActionInterceptor, TGEventListener {
    private val ids = mutableListOf<String>()

    fun containsActionId(id: String) = ids.contains(id)
    fun addActionId(id: String) = ids.add(id)
    fun removeActionId(id: String) = ids.remove(id)

    override fun intercept(id: String, context: TGActionContext): Boolean {
        // Reposting an unlocked UI action would intercept it forever.
        if (containsActionId(id) && isUiThread() && TGEditorManager.getInstance(getContext()).isLocked) {
            runInUiThread(id, context)
            return true
        }
        return false
    }

    override fun processEvent(event: TGEvent) {
        val id = event.getAttribute<String>(TGActionEvent.ATTRIBUTE_ACTION_ID)
        if (!containsActionId(id)) return
        val editor = TGEditorManager.getInstance(getContext())
        when (event.eventType) {
            TGActionPreExecutionEvent.EVENT_TYPE -> editor.lock()
            TGActionPostExecutionEvent.EVENT_TYPE, TGActionErrorEvent.EVENT_TYPE -> editor.unlock()
        }
    }
}
