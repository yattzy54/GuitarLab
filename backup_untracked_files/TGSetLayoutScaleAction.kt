package app.tuxguitar.android.action.impl.layout

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.util.TGContext

class TGSetLayoutScaleAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        TGSongViewController.getInstance(getContext()).scale(
            actionContext.getAttribute<Float>(ATTRIBUTE_SCALE)
        )
    }

    companion object {
        const val NAME = "action.view.layout-set-scale"
        const val ATTRIBUTE_SCALE = "scale"
    }
}
