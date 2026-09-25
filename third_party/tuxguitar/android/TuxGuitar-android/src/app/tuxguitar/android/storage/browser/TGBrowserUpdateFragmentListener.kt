package app.tuxguitar.android.storage.browser

import app.tuxguitar.action.TGActionPostExecutionEvent
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.android.fragment.TGFragmentController
import app.tuxguitar.android.fragment.impl.TGBrowserFragmentController
import app.tuxguitar.android.fragment.impl.TGMainFragmentController
import app.tuxguitar.editor.action.file.TGReadSongAction
import app.tuxguitar.editor.action.file.TGWriteSongAction
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.TGException
import app.tuxguitar.util.TGSynchronizer

class TGBrowserUpdateFragmentListener(private val context: TGContext) : TGEventListener {
    private val actionMap: MutableMap<String, TGFragmentController<*>> = HashMap()

    init {
        fillActionMap()
    }

    fun findActivity(): TGActivity? = TGActivityController.getInstance(context).activity

    fun fillActionMap() {
        actionMap[TGReadSongAction.NAME] = TGMainFragmentController.getInstance(context)
        actionMap[TGWriteSongAction.NAME] = TGMainFragmentController.getInstance(context)
        actionMap[TGBrowserPrepareForReadAction.NAME] = TGBrowserFragmentController.getInstance(context)
        actionMap[TGBrowserPrepareForWriteAction.NAME] = TGBrowserFragmentController.getInstance(context)
    }

    fun checkForFragmentToOpen(event: TGEvent) {
        val actionId = event.getAttribute<String>(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID)
        val activity = findActivity() ?: return
        val controller = actionMap[actionId] ?: return
        activity.getNavigationManager().callOpenFragment(controller)
    }

    override fun processEvent(event: TGEvent) {
        if (TGActionPostExecutionEvent.EVENT_TYPE == event.eventType) {
            TGSynchronizer.getInstance(context).executeLater {
                checkForFragmentToOpen(event)
            }
        }
    }
}
