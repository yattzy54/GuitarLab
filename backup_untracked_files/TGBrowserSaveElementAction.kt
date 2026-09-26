package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionException
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.editor.action.file.TGReadSongAction
import app.tuxguitar.editor.action.file.TGWriteSongAction
import app.tuxguitar.io.base.TGFileFormat
import app.tuxguitar.util.TGContext
import java.io.OutputStream

class TGBrowserSaveElementAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val session = actionContext.getAttribute<TGBrowserSession>(ATTRIBUTE_SESSION)
        val element = actionContext.getAttribute<TGBrowserElement>(ATTRIBUTE_ELEMENT)
        val format = actionContext.getAttribute<TGFileFormat>(TGReadSongAction.ATTRIBUTE_FORMAT)
        session.browser!!.getOutputStream(object :
            TGBrowserActionCallBack<OutputStream>(this@TGBrowserSaveElementAction, actionContext) {
            override fun onActionSuccess(actionContext: TGActionContext, successData: OutputStream) {
                try {
                    successData.use { stream ->
                        actionContext.setAttribute(TGWriteSongAction.ATTRIBUTE_OUTPUT_STREAM, stream)
                        TGActionManager.getInstance(getContext()).execute(TGWriteSongAction.NAME, actionContext)
                        session.currentElement = element
                        session.currentFormat = format
                    }
                } catch (throwable: Throwable) {
                    throw TGActionException(throwable)
                }
            }
        }, element)
    }
    companion object {
        const val NAME = "action.browser.save-element"
        const val ATTRIBUTE_ELEMENT = "app.tuxguitar.android.browser.model.TGBrowserElement"
        const val ATTRIBUTE_SESSION = "app.tuxguitar.android.browser.model.TGBrowserSession"
        const val ATTRIBUTE_FORMAT = "format"
        const val ATTRIBUTE_FORMAT_CODE = "formatCode"
    }
}
