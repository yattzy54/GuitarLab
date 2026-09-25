package app.tuxguitar.android.view.browser

import android.view.View
import android.widget.AdapterView
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.browser.TGBrowserCdElementAction
import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.io.base.TGFileFormatUtils
import app.tuxguitar.util.error.TGErrorManager

class TGBrowserItemListener(private val browserView: TGBrowserView) : AdapterView.OnItemClickListener {

    override fun onItemClick(adapter: AdapterView<*>, view: View, position: Int, id: Long) {
        this.processElementAction(view.tag as TGBrowserElement)
    }

    fun processElementAction(element: TGBrowserElement) {
        try {
            if (element.isFolder()) {
                this.processCdElementAction(element)
            } else {
                val browserSession: TGBrowserSession = TGBrowserManager.getInstance(this.browserView.findContext()).session
                if (browserSession.sessionType == TGBrowserSession.READ_MODE) {
                    this.processOpenElementAction(element)
                }
                if (browserSession.sessionType == TGBrowserSession.WRITE_MODE && element.isWritable()) {
                    this.processSaveElementAction(element)
                }
            }
        } catch (e: TGBrowserException) {
            TGErrorManager.getInstance(this.browserView.findContext()).handleError(e)
        }
    }

    fun processCdElementAction(element: TGBrowserElement) {
        this.browserView.actionHandler.createBrowserElementAction(TGBrowserCdElementAction.NAME, element).process()
    }

    fun processOpenElementAction(element: TGBrowserElement) {
        val formatCode = TGFileFormatUtils.getFileFormatCode(element.getName())
        this.browserView.actionHandler.createBrowserOpenElementAction(element, formatCode).process()
    }

    @Throws(TGBrowserException::class)
    fun processSaveElementAction(element: TGBrowserElement) {
        val confirmMessage = this.browserView.findActivity().getString(R.string.browser_file_overwrite_question)
        val formatCode = TGFileFormatUtils.getFileFormatCode(element.getName())
        val actionProcessor: TGActionProcessor = this.browserView.actionHandler.createBrowserSaveElementAction(element, formatCode)
        this.browserView.actionHandler.processConfirmableAction(actionProcessor, confirmMessage)
    }
}
