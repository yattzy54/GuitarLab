package app.tuxguitar.android.view.browser

import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.editor.event.TGDestroyEvent
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventException
import app.tuxguitar.event.TGEventListener

class TGBrowserDestroyListener(private val browser: TGBrowserView) : TGEventListener {

    override fun processEvent(event: TGEvent) {
        if (TGDestroyEvent.EVENT_TYPE == event.eventType) {
            try {
                this.browser.onDestroy()
            } catch (e: TGBrowserException) {
                throw TGEventException(e)
            }
        }
    }
}
