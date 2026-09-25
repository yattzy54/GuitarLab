package app.tuxguitar.android.storage

import app.tuxguitar.action.TGActionPostExecutionEvent
import app.tuxguitar.editor.action.file.TGLoadSongAction
import app.tuxguitar.editor.action.file.TGWriteSongAction
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.util.TGAbstractContext
import app.tuxguitar.util.TGContext

class TGStorageEventListener(private val context: TGContext) : TGEventListener {
    fun isUpdatableAction(actionId: String): Boolean = actionId in UPDATABLE_ACTION_IDS

    fun checkForUpdateSession(event: TGEvent) {
        val actionId = event.getAttribute<String>(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID)
        if (isUpdatableAction(actionId)) {
            TGStorageManager.getInstance(context).updateSession(
                event.getAttribute<TGAbstractContext>(
                    TGActionPostExecutionEvent.ATTRIBUTE_SOURCE_CONTEXT,
                ),
            )
        }
    }

    override fun processEvent(event: TGEvent) {
        if (TGActionPostExecutionEvent.EVENT_TYPE == event.eventType) {
            checkForUpdateSession(event)
        }
    }

    companion object {
        private val UPDATABLE_ACTION_IDS = setOf(TGLoadSongAction.NAME, TGWriteSongAction.NAME)
    }
}
