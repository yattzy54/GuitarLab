package app.tuxguitar.android.view.dialog.browser.collection

import app.tuxguitar.action.TGActionPostExecutionEvent
import app.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction
import app.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.util.TGSynchronizer

class TGBrowserCollectionsEventListener(private val dialog: TGBrowserCollectionsDialog) : TGEventListener {
    fun isRefreshableAction(actionId: String): Boolean =
        REFRESHABLE_ACTIONS.contains(actionId)

    fun processPostExecution(id: String) {
        if (isRefreshableAction(id)) dialog.refreshListView()
    }

    override fun processEvent(event: TGEvent) {
        if (TGActionPostExecutionEvent.EVENT_TYPE == event.eventType) {
            val id = event.getAttribute<String>(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID)!!
            TGSynchronizer.getInstance(dialog.findContext()).executeLater {
                processPostExecution(id)
            }
        }
    }

    companion object {
        private val REFRESHABLE_ACTIONS = arrayOf(
            TGBrowserAddCollectionAction.NAME,
            TGBrowserRemoveCollectionAction.NAME
        )
    }
}
