package app.tuxguitar.android.storage.browser

import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveCurrentElementAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.android.storage.TGStorageProvider
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.io.base.TGFileFormat
import app.tuxguitar.util.TGAbstractContext
import app.tuxguitar.util.TGContext

class TGBrowserProvider(private val context: TGContext) : TGStorageProvider {
    init { createListeners() }

    fun createListeners() {
        TGActionManager.getInstance(context).addPostExecutionListener(TGBrowserUpdateFragmentListener(context))
    }

    override fun openDocument() { createOpenFileAction().process() }
    override fun saveDocument() { createSaveFileAction().process() }
    override fun saveDocumentAs() { createSaveFileAsAction().process() }

    override fun updateSession(source: TGAbstractContext) {
        val session = findBrowserSession()
        session.currentFormat = source.getAttribute(TGFileFormat::class.java.name) as? TGFileFormat
        session.currentElement = source.getAttribute(TGBrowserElement::class.java.name) as? TGBrowserElement
    }

    fun findBrowserSession(): TGBrowserSession = TGBrowserManager.getInstance(context).session

    fun createAction(actionId: String): TGActionProcessor = TGActionProcessor(context, actionId)

    fun createBrowserAction(actionId: String): TGActionProcessor {
        val tgActionProcessor = createAction(actionId)
        tgActionProcessor.setAttribute(TGBrowserSession::class.java.name, findBrowserSession())
        tgActionProcessor.setAttribute(TGActivity::class.java.name, TGActivityController.getInstance(context).activity)
        return tgActionProcessor
    }

    fun createOpenFileAction(): TGActionProcessor = createBrowserAction(TGBrowserPrepareForReadAction.NAME)
    fun createSaveFileAsAction(): TGActionProcessor = createBrowserAction(TGBrowserPrepareForWriteAction.NAME)
    fun createSaveFileAction(): TGActionProcessor = createBrowserAction(TGBrowserSaveCurrentElementAction.NAME)
}
