package app.tuxguitar.android.transport

import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.player.base.MidiPlayerEvent
import app.tuxguitar.thread.TGThreadLoop
import app.tuxguitar.thread.TGThreadManager
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.error.TGErrorManager

class TGTransportListener(private val context: TGContext) : TGEventListener {
    fun startLoop() {
        TGThreadManager.getInstance(context).loop(object : TGThreadLoop {
            override fun process(): Long = if (processLoop()) 25L else TGThreadLoop.BREAK
        })
    }

    fun processLoop(): Boolean {
        try {
            val tgEditorManager = TGEditorManager.getInstance(context)
            val tgTransport = TGTransport.getInstance(context)
            val midiPlayer = MidiPlayer.getInstance(context)
            if (midiPlayer.isRunning) {
                tgEditorManager.lock()
                try {
                    tgTransport.cache.updatePlayMode()
                } finally {
                    tgEditorManager.unlock()
                }

                if (tgTransport.cache.shouldRedraw()) {
                    tgEditorManager.redrawPlayingNewBeat()
                }
                return true
            }
            notifyStopped()
        } catch (throwable: Throwable) {
            TGErrorManager.getInstance(context).handleError(throwable)
        }
        return false
    }

    fun notifyStarted() {
        val tgEditorManager = TGEditorManager.getInstance(context)
        tgEditorManager.asyncRunLocked {
            try {
                val tgTransport = TGTransport.getInstance(context)
                tgTransport.cache.reset()

                TGActivityController.getInstance(context).activity?.let { activity ->
                    activity.updateCache(true)
                    activity.setDisplayOn(true)
                }

                startLoop()
            } catch (throwable: Throwable) {
                TGErrorManager.getInstance(context).handleError(throwable)
            }
        }
    }

    fun notifyStopped() {
        val tgEditorManager = TGEditorManager.getInstance(context)
        tgEditorManager.asyncRunLocked {
            try {
                val tgTransport = TGTransport.getInstance(context)
                tgTransport.gotoPlayerPosition()
                tgTransport.cache.reset()
                TGActivityController.getInstance(context).activity?.setDisplayOn(false)
            } catch (throwable: Throwable) {
                TGErrorManager.getInstance(context).handleError(throwable)
            }
        }
    }

    override fun processEvent(event: TGEvent) {
        if (MidiPlayerEvent.EVENT_TYPE == event.eventType) {
            val type = event.getAttribute(MidiPlayerEvent.PROPERTY_NOTIFICATION_TYPE) as Int
            if (type == MidiPlayerEvent.NOTIFY_STARTED) {
                notifyStarted()
            } else if (type == MidiPlayerEvent.NOTIFY_STOPPED) {
                notifyStopped()
            }
        }
    }
}
