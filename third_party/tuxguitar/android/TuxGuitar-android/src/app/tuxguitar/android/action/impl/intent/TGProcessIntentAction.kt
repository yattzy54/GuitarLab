package app.tuxguitar.android.action.impl.intent

import android.content.Intent
import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionException
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.action.impl.storage.uri.TGUriReadAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.util.TGContext

class TGProcessIntentAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        try {
            val activity = actionContext.getAttribute<TGActivity>(ATTRIBUTE_ACTIVITY)
            val intent = activity.intent
            if (intent != null && intent.action == Intent.ACTION_VIEW) {
                actionContext.setAttribute(TGUriReadAction.ATTRIBUTE_URI, intent.data)
                TGActionManager.getInstance(getContext()).execute(TGUriReadAction.NAME, actionContext)
            }
        } catch (throwable: Throwable) {
            throw TGActionException(throwable)
        }
    }

    companion object {
        const val NAME = "action.intent.process"
        const val ATTRIBUTE_ACTIVITY = "app.tuxguitar.android.activity.TGActivity"
    }
}
