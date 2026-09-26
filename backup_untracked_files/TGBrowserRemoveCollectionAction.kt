package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionException
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.tools.browser.TGBrowserCollection
import app.tuxguitar.util.TGContext

class TGBrowserRemoveCollectionAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        try {
            val manager = TGBrowserManager.getInstance(getContext())
            manager.removeCollection(actionContext.getAttribute(ATTRIBUTE_COLLECTION))
            manager.storeCollections()
        } catch (exception: Exception) {
            throw TGActionException(exception)
        }
    }
    companion object {
        const val NAME = "action.browser.remove-collection"
        const val ATTRIBUTE_COLLECTION = "app.tuxguitar.tools.browser.TGBrowserCollection"
    }
}
