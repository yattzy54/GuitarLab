package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionException
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.editor.action.file.TGReadSongAction
import app.tuxguitar.io.base.TGFileFormat
import app.tuxguitar.util.TGContext
import java.io.InputStream

class TGBrowserOpenElementAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val session = actionContext.getAttribute<TGBrowserSession>(ATTRIBUTE_SESSION)
        val element = actionContext.getAttribute<TGBrowserElement>(ATTRIBUTE_ELEMENT)
        session.browser?.getInputStream(object :
            TGBrowserActionCallBack<InputStream>(this@TGBrowserOpenElementAction, actionContext) {
            override fun onActionSuccess(actionContext: TGActionContext, successData: InputStream) {
                try {
                    successData.use { stream ->
                        actionContext.setAttribute(TGReadSongAction.ATTRIBUTE_INPUT_STREAM, stream)
                        TGActionManager.getInstance(getContext()).execute(TGReadSongAction.NAME, actionContext)
                        session.currentElement = element
                        session.currentFormat = actionContext.getAttribute<TGFileFormat>(TGReadSongAction.ATTRIBUTE_FORMAT)
                    }
                } catch (throwable: Throwable) {
                    throw TGActionException(throwable)
                }
            }
        }, element)
    }
    companion object {
        const val NAME = "action.browser.open-element"
        const val ATTRIBUTE_ELEMENT = "app.tuxguitar.android.browser.model.TGBrowserElement"
        const val ATTRIBUTE_SESSION = "app.tuxguitar.android.browser.model.TGBrowserSession"
        const val ATTRIBUTE_FORMAT_CODE = "formatCode"
    }
}
