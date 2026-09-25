package app.tuxguitar.android.action.impl.gui

import android.content.Intent
import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.util.TGContext

class TGStartActivityForResultAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val activity = actionContext.getAttribute<TGActivity>(ATTRIBUTE_ACTIVITY)
        val intent = actionContext.getAttribute<Intent>(ATTRIBUTE_INTENT)
        val requestCode = actionContext.getAttribute<Int>(ATTRIBUTE_REQUEST_CODE)
        @Suppress("DEPRECATION")
        activity.startActivityForResult(intent, requestCode)
    }

    companion object {
        const val NAME = "action.gui.start-activity-for-result"
        const val ATTRIBUTE_ACTIVITY = "app.tuxguitar.android.activity.TGActivity"
        const val ATTRIBUTE_INTENT = "android.content.Intent"
        const val ATTRIBUTE_REQUEST_CODE = "requestCode"
    }
}
