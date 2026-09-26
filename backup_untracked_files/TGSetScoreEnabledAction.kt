package app.tuxguitar.android.action.impl.layout

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.graphics.control.TGLayout
import app.tuxguitar.util.TGContext

class TGSetScoreEnabledAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(context: TGActionContext) {
        val layout = TGSongViewController.getInstance(getContext()).layout
        layout.style = layout.style xor TGLayout.DISPLAY_SCORE
        if (layout.style and TGLayout.DISPLAY_TABLATURE == 0 && layout.style and TGLayout.DISPLAY_SCORE == 0) {
            layout.style = layout.style xor TGLayout.DISPLAY_TABLATURE
        }
    }

    companion object {
        const val NAME = "action.view.layout-set-score-enabled"
        const val ATTRIBUTE_SCALE = "scale"
    }
}
