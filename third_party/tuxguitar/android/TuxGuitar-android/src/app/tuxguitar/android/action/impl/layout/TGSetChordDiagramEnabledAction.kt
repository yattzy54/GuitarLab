package app.tuxguitar.android.action.impl.layout

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.graphics.control.TGLayout
import app.tuxguitar.util.TGContext

class TGSetChordDiagramEnabledAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        val layout = TGSongViewController.getInstance(getContext()).layout
        layout.style = layout.style xor TGLayout.DISPLAY_CHORD_DIAGRAM
    }

    companion object {
        const val NAME = "action.view.layout-set-chord-diagram-enabled"
        const val ATTRIBUTE_SCALE = "scale"
    }
}
