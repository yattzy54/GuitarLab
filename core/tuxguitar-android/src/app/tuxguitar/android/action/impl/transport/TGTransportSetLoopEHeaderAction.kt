package app.tuxguitar.android.action.impl.transport

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.util.TGContext

class TGTransportSetLoopEHeaderAction(context: TGContext) : TGActionBase(context, NAME) {
 override fun processAction(context: TGActionContext) {
  val mode = MidiPlayer.getInstance(getContext()).mode
  val caretMeasure = TGSongViewController.getInstance(getContext()).caret.measure
  val caretNumber = caretMeasure?.number ?: -1
  val measureNumber = if (mode.loopEHeader != caretNumber) caretNumber else -1
  mode.loopEHeader = measureNumber
  if (mode.loopSHeader != -1 && mode.loopEHeader != -1 && mode.loopSHeader > measureNumber) mode.loopSHeader = measureNumber
 }
 companion object { const val NAME = "action.transport.set-loop-end" }
}
