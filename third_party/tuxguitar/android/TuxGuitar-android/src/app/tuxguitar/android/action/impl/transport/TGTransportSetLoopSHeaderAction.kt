package app.tuxguitar.android.action.impl.transport

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.util.TGContext

class TGTransportSetLoopSHeaderAction(context: TGContext) : TGActionBase(context, NAME) {
 override fun processAction(context: TGActionContext) {
  val mode = MidiPlayer.getInstance(getContext()).mode
  val caretNumber = TGSongViewController.getInstance(getContext()).caret.measure.number
  val measureNumber = if (mode.loopSHeader != caretNumber) caretNumber else -1
  mode.loopSHeader = measureNumber
  if (mode.loopEHeader != -1 && mode.loopSHeader != -1 && mode.loopEHeader < measureNumber) mode.loopEHeader = measureNumber
 }
 companion object { const val NAME = "action.transport.set-loop-start" }
}
