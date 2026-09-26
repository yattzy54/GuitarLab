package app.tuxguitar.android.view.browser

import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.browser.TGBrowserCdElementAction
import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.android.fragment.impl.TGBrowserFragment
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.io.base.TGFileFormatUtils
import app.tuxguitar.util.error.TGErrorManager

class TGBrowserItemListener(private val browserFragment: TGBrowserFragment) {

    fun processElementAction(element: TGBrowserElement) {
        try {
            if (element.isFolder()) {
                processCdElementAction(element)
            } else {
                val browserSession: TGBrowserSession = TGBrowserManager.getInstance(browserFragment.findContext()).session
                if (browserSession.sessionType == TGBrowserSession.READ_MODE) {
                    processOpenElementAction(element)
                }
                if (browserSession.sessionType == TGBrowserSession.WRITE_MODE && element.isWritable()) {
                    processSaveElementAction(element)
                }
            }
        } catch (e: TGBrowserException) {
            TGErrorManager.getInstance(browserFragment.findContext()).handleError(e)
        }
    }

    fun processCdElementAction(element: TGBrowserElement) {
        browserFragment.actionHandler.createBrowserElementAction(TGBrowserCdElementAction.NAME, element).process()
    }

    fun processOpenElementAction(element: TGBrowserElement) {
        val formatCode = TGFileFormatUtils.getFileFormatCode(element.getName())
        browserFragment.actionHandler.createBrowserOpenElementAction(element, formatCode).process()
    }

    @Throws(TGBrowserException::class)
    fun processSaveElementAction(element: TGBrowserElement) {
        val confirmMessage = browserFragment.findActivity().getString(R.string.browser_file_overwrite_question)
        val formatCode = TGFileFormatUtils.getFileFormatCode(element.getName())
        val actionProcessor: TGActionProcessor = browserFragment.actionHandler.createBrowserSaveElementAction(element, formatCode)
        browserFragment.actionHandler.processConfirmableAction(actionProcessor, confirmMessage)
    }
}
