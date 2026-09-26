package app.tuxguitar.android.action.impl.layout

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.graphics.control.TGLayout
import app.tuxguitar.util.TGContext

class TGToggleHighlightPlayedBeatAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        val layout = TGSongViewController.getInstance(getContext()).layout
        layout.style = layout.style xor TGLayout.HIGHLIGHT_PLAYED_BEAT
    }

    companion object {
        const val NAME = "action.transport.highlight-played-beat"
    }
}
