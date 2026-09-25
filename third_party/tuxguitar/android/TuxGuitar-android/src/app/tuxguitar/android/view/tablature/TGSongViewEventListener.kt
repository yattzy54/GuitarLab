package app.tuxguitar.android.view.tablature

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
    fun processUpdateEvent(event: TGEvent) {
        when (event.getAttribute<Int>(TGUpdateEvent.PROPERTY_UPDATE_MODE)) {
            TGUpdateEvent.SELECTION -> {
                songView.updateSelection()
                val sourceContext =
                    event.getAttribute<TGAbstractContext?>(TGEvent.ATTRIBUTE_SOURCE_CONTEXT)
                if (sourceContext?.getAttribute<Boolean>(TGSongViewSmartMenu.REQUEST_SMART_MENU) == true) {
                    songView.smartMenu.openSmartMenu(sourceContext)
                }
            }
            TGUpdateEvent.MEASURE_UPDATED -> {
                val measureNumbers =
                    event.getAttribute<List<Int>>(TGUpdateMeasuresEvent.PROPERTY_MEASURE_NUMBERS)
                songView.updateMeasures(measureNumbers)
            }
            TGUpdateEvent.SONG_UPDATED -> songView.updateTablature()
            TGUpdateEvent.SONG_LOADED -> {
                songView.updateTablature()
                songView.resetScroll()
                songView.resetCaret()
            }
        }
    }

    fun processRedrawEvent(event: TGEvent) {
        when (event.getAttribute<Int>(TGRedrawEvent.PROPERTY_REDRAW_MODE)) {
            TGRedrawEvent.NORMAL -> {
                songView.redraw()
                requestTempoDisplayUpdate()
            }
            TGRedrawEvent.PLAYING_NEW_BEAT -> {
                songView.redrawPlayingMode()
                requestTempoDisplayUpdate()
            }
        }
    }

    private fun requestTempoDisplayUpdate() {
        val activity = TGActivityController.getInstance(songView.context).activity ?: return
        activity.runOnUiThread {
            TGMainMenu.getInstance(songView.context).updateTempoDisplay()
        }
    }

    fun processDestroyEvent(event: TGEvent) {
        songView.dispose()
    }

    override fun processEvent(event: TGEvent) {
        when (event.eventType) {
            TGRedrawEvent.EVENT_TYPE -> processRedrawEvent(event)
            TGUpdateEvent.EVENT_TYPE -> processUpdateEvent(event)
            TGDestroyEvent.EVENT_TYPE -> processDestroyEvent(event)
        }
    }
}
