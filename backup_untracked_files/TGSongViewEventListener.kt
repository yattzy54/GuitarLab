package app.tuxguitar.android.view.tablature

import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.android.menu.controller.impl.fragment.TGMainMenu
import app.tuxguitar.editor.event.TGDestroyEvent
import app.tuxguitar.editor.event.TGRedrawEvent
import app.tuxguitar.editor.event.TGUpdateEvent
import app.tuxguitar.editor.event.TGUpdateMeasuresEvent
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.util.TGAbstractContext

class TGSongViewEventListener(private val songView: TGSongViewController) : TGEventListener {

    override fun processEvent(event: TGEvent) {
        when (event.eventType) {
            TGRedrawEvent.EVENT_TYPE -> this.processRedrawEvent(event)
            TGUpdateEvent.EVENT_TYPE -> this.processUpdateEvent(event)
            TGDestroyEvent.EVENT_TYPE -> this.processDestroyEvent(event)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun processUpdateEvent(event: TGEvent) {
        val type = (event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE) as Int)
        when (type) {
            TGUpdateEvent.SELECTION -> {
                this.songView.updateSelection()

                val sourceContext = event.getAttribute(TGEvent.ATTRIBUTE_SOURCE_CONTEXT) as? TGAbstractContext
                if (sourceContext != null && java.lang.Boolean.TRUE == sourceContext.getAttribute(TGSongViewSmartMenu.REQUEST_SMART_MENU)) {
                    this.songView.smartMenu.openSmartMenu(sourceContext)
                }
            }
            TGUpdateEvent.MEASURE_UPDATED -> this.songView.updateMeasures(event.getAttribute(TGUpdateMeasuresEvent.PROPERTY_MEASURE_NUMBERS) as List<Int>)
            TGUpdateEvent.SONG_UPDATED -> this.songView.updateTablature()
            TGUpdateEvent.SONG_LOADED -> {
                this.songView.updateTablature()
                this.songView.resetScroll()
                this.songView.resetCaret()
            }
        }
    }

    fun processRedrawEvent(event: TGEvent) {
        val type = (event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE) as Int)
        when (type) {
            TGRedrawEvent.NORMAL -> {
                this.songView.redraw()
                this.requestTempoDisplayUpdate()
            }
            TGRedrawEvent.PLAYING_NEW_BEAT -> {
                this.songView.redrawPlayingMode()
                this.requestTempoDisplayUpdate()
            }
        }
    }

    private fun requestTempoDisplayUpdate() {
        val activity: TGActivity? = TGActivityController.getInstance(this.songView.context).activity
        if (activity != null) {
            activity.requireActivity().runOnUiThread {
                TGMainMenu.getInstance(this.songView.context).updateTempoDisplay()
            }
        }
    }

    fun processDestroyEvent(event: TGEvent) {
        this.songView.dispose()
    }
}
