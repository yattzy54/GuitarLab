package app.tuxguitar.android.view.channel

import app.tuxguitar.editor.event.TGUpdateEvent
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener

class TGChannelEventListener(private val channelList: TGChannelListView) : TGEventListener {

    override fun processEvent(event: TGEvent) {
        if (TGUpdateEvent.EVENT_TYPE == event.eventType) {
            val type = (event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE) as Int)
            if (type == TGUpdateEvent.SELECTION) {
                this.channelList.fireUpdateProcess()
            }
        }
    }
}
