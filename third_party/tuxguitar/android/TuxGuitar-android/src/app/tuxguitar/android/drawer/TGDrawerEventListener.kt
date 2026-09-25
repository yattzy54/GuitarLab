package app.tuxguitar.android.drawer

import app.tuxguitar.action.TGActionPostExecutionEvent
import app.tuxguitar.android.navigation.TGNavigationEvent
import app.tuxguitar.android.navigation.TGNavigationFragment
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.util.TGSynchronizer

class TGDrawerEventListener(private val drawerManager: TGDrawerManager) : TGEventListener {
    fun closeDrawer() {
        drawerManager.closeDrawer()
    }

    fun processNavigationEvent(event: TGEvent) {
        val fragment = event.getAttribute(TGNavigationEvent.PROPERTY_LOADED_FRAGMENT) as TGNavigationFragment
        drawerManager.onOpenFragment(fragment.controller!!)
    }

    override fun processEvent(event: TGEvent) {
        TGSynchronizer.getInstance(drawerManager.findContext()).executeLater {
            if (TGNavigationEvent.EVENT_TYPE == event.eventType) {
                processNavigationEvent(event)
            }
            if (TGActionPostExecutionEvent.EVENT_TYPE == event.eventType) {
                closeDrawer()
            }
        }
    }
}
