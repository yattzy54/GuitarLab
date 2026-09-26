package app.tuxguitar.android.view.browser

import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.android.fragment.impl.TGBrowserFragment
import app.tuxguitar.editor.event.TGDestroyEvent
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventException
import app.tuxguitar.event.TGEventListener

class TGBrowserDestroyListener(private val browser: TGBrowserFragment) : TGEventListener {

    override fun processEvent(event: TGEvent) {
        if (TGDestroyEvent.EVENT_TYPE == event.eventType) {
            try {
                browser.onDestroyBrowser()
            } catch (e: TGBrowserException) {
                throw TGEventException(e)
            }
        }
    }
}
