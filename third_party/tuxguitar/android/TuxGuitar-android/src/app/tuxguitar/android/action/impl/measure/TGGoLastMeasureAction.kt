package app.tuxguitar.android.action.impl.measure

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.transport.TGTransport
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.util.TGContext

class TGGoLastMeasureAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        val midiPlayer = MidiPlayer.getInstance(getContext())
        if (midiPlayer.isRunning) TGTransport.getInstance(getContext()).gotoLast()
        else {
            val caret = getEditor().caret
            val track = caret.track
            val measure = getSongManager(context).trackManager.getLastMeasure(track)
            if (track != null && measure != null) {
                caret.update(track.number, measure.start, caret.selectedString.number)
            }
        }
    }
    companion object { const val NAME = "action.measure.go-last" }
}
