package app.tuxguitar.android.view.browser

import app.tuxguitar.action.TGActionErrorEvent
import app.tuxguitar.action.TGActionEvent
import app.tuxguitar.action.TGActionPostExecutionEvent
import app.tuxguitar.android.action.TGActionAsyncProcessErrorEvent
import app.tuxguitar.android.action.TGActionAsyncProcessFinishEvent
import app.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction
import app.tuxguitar.android.action.impl.browser.TGBrowserCdElementAction
import app.tuxguitar.android.action.impl.browser.TGBrowserCdRootAction
import app.tuxguitar.android.action.impl.browser.TGBrowserCdUpAction
import app.tuxguitar.android.action.impl.browser.TGBrowserCloseSessionAction
import app.tuxguitar.android.action.impl.browser.TGBrowserLoadSessionAction
import app.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction
import app.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveElementAction
import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.util.TGException
import app.tuxguitar.util.TGSynchronizer

class TGBrowserEventListener(private val browser: TGBrowserView) : TGEventListener {

    override fun processEvent(event: TGEvent) {
        TGSynchronizer.getInstance(this.browser.findContext()).executeLater {
            try {
                val actionId = event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID) as String
                when (event.eventType) {
                    TGActionPostExecutionEvent.EVENT_TYPE -> processPostExecution(actionId, REFRESHABLE_ACTIONS)
                    TGActionAsyncProcessFinishEvent.EVENT_TYPE -> processPostExecution(actionId, REFRESHABLE_ASYNC_ACTIONS)
                    TGActionErrorEvent.EVENT_TYPE -> processError(actionId, REFRESHABLE_ACTIONS)
                    TGActionAsyncProcessErrorEvent.EVENT_TYPE -> processError(actionId, REFRESHABLE_ASYNC_ACTIONS)
                }
            } catch (e: TGBrowserException) {
                throw TGException(e)
            }
        }
    }

    fun isRefreshableAction(actionId: String, refreshableActionIds: Array<String>): Boolean {
        for (refreshableActionId in refreshableActionIds) {
            if (refreshableActionId == actionId) {
                return true
            }
        }
        return false
    }

    @Throws(TGBrowserException::class)
    fun processPostExecution(id: String, refreshableActionIds: Array<String>) {
        when {
            TGBrowserRefreshAction.NAME == id -> this.browser.refresh()
            isRefreshableAction(id, refreshableActionIds) -> this.browser.requestRefresh()
        }
    }

    @Throws(TGBrowserException::class)
    fun processError(id: String, refreshableActionIds: Array<String>) {
        if (TGBrowserRefreshAction.NAME == id || isRefreshableAction(id, refreshableActionIds)) {
            this.browser.refresh()
        }
    }

    companion object {
        private val REFRESHABLE_ACTIONS = arrayOf(
            TGBrowserCloseSessionAction.NAME,
            TGBrowserAddCollectionAction.NAME,
            TGBrowserRemoveCollectionAction.NAME
        )

        private val REFRESHABLE_ASYNC_ACTIONS = arrayOf(
            TGBrowserCdRootAction.NAME,
            TGBrowserCdUpAction.NAME,
            TGBrowserCdElementAction.NAME,
            TGBrowserSaveElementAction.NAME,
            TGBrowserLoadSessionAction.NAME
        )
    }
}
