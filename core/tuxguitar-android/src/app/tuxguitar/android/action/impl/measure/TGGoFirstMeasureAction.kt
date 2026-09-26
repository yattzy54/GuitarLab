package app.tuxguitar.android.action.impl.measure

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.transport.TGTransport
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.util.TGContext

class TGGoFirstMeasureAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        val midiPlayer = MidiPlayer.getInstance(getContext())
        if (midiPlayer.isRunning) TGTransport.getInstance(getContext()).gotoFirst()
        else {
            val caret = getEditor().caret
            val track = caret.track
            val selectedString = caret.selectedString
            val measure = getSongManager(context).trackManager.getFirstMeasure(track)
            if (track != null && measure != null && selectedString != null) {
                caret.update(track.number, measure.start, selectedString.number)
            }
        }
    }
    companion object { const val NAME = "action.measure.go-first" }
}
