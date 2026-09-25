package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionException
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.android.browser.model.TGBrowser
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.tools.browser.TGBrowserCollection
import app.tuxguitar.util.TGContext

class TGBrowserLoadSessionAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val browser = actionContext.getAttribute<TGBrowser>(ATTRIBUTE_BROWSER)
        val collection = actionContext.getAttribute<TGBrowserCollection>(ATTRIBUTE_COLLECTION)
        val session = actionContext.getAttribute<TGBrowserSession>(ATTRIBUTE_SESSION)
        session.browser = browser
        browser?.open(object : TGBrowserActionCallBack<Any>(this@TGBrowserLoadSessionAction, actionContext) {
            override fun onActionSuccess(actionContext: TGActionContext, successData: Any) {
                session.browser?.cdRoot(object :
                    TGBrowserActionCallBack<Any>(this@TGBrowserLoadSessionAction, actionContext) {
                    override fun onActionSuccess(actionContext: TGActionContext, successData: Any) {
                        try {
                            session.collection = collection
                            TGBrowserManager.getInstance(getContext()).storeDefaultCollection()
                            TGActionManager.getInstance(getContext()).execute(TGBrowserRefreshAction.NAME, actionContext)
                        } catch (exception: Exception) {
                            throw TGActionException(exception)
                        }
                    }
                })
            }
        })
    }
    companion object {
        const val NAME = "action.browser.load-session"
        const val ATTRIBUTE_SESSION = "app.tuxguitar.android.browser.model.TGBrowserSession"
        const val ATTRIBUTE_COLLECTION = "app.tuxguitar.tools.browser.TGBrowserCollection"
        const val ATTRIBUTE_BROWSER = "app.tuxguitar.android.browser.model.TGBrowser"
    }
}
