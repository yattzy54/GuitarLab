package app.tuxguitar.android.view.browser

import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.action.impl.browser.TGBrowserCloseSessionAction
import app.tuxguitar.android.action.impl.browser.TGBrowserOpenElementAction
import app.tuxguitar.android.action.impl.browser.TGBrowserOpenSessionAction
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveElementAction
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveNewElementAction
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.android.view.dialog.TGDialogController
import app.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.io.base.TGFileFormat
import app.tuxguitar.tools.browser.TGBrowserCollection

class TGBrowserActionHandler(private val browser: TGBrowserView) {

    fun createAction(actionId: String): TGActionProcessorListener {
        return TGActionProcessorListener(this.browser.findContext(), actionId)
    }

    fun createBrowserAction(actionId: String): TGActionProcessorListener {
        val tgBrowserSession: TGBrowserSession = TGBrowserManager.getInstance(this.browser.findContext()).session
        val tgActionProcessor = this.createAction(actionId)
        tgActionProcessor.setAttribute(TGBrowserSession::class.java.name, tgBrowserSession)
        return tgActionProcessor
    }

    fun createBrowserElementAction(actionId: String, element: TGBrowserElement): TGActionProcessorListener {
        val tgActionProcessor = this.createBrowserAction(actionId)
        tgActionProcessor.setAttribute(TGBrowserElement::class.java.name, element)
        return tgActionProcessor
    }

    fun createBrowserOpenElementAction(element: TGBrowserElement, formatCode: String): TGActionProcessorListener {
        val tgActionProcessor = this.createBrowserElementAction(TGBrowserOpenElementAction.NAME, element)
        tgActionProcessor.setAttribute(TGBrowserOpenElementAction.ATTRIBUTE_FORMAT_CODE, formatCode)
        return tgActionProcessor
    }

    fun createBrowserSaveElementAction(element: TGBrowserElement, formatCode: String): TGActionProcessorListener {
        val tgActionProcessor = this.createBrowserElementAction(TGBrowserSaveElementAction.NAME, element)
        tgActionProcessor.setAttribute(TGBrowserSaveElementAction.ATTRIBUTE_FORMAT_CODE, formatCode)
        return tgActionProcessor
    }

    fun createBrowserSaveElementAction(element: TGBrowserElement, format: TGFileFormat): TGActionProcessorListener {
        val tgActionProcessor = this.createBrowserElementAction(TGBrowserSaveElementAction.NAME, element)
        tgActionProcessor.setAttribute(TGBrowserSaveElementAction.ATTRIBUTE_FORMAT, format)
        return tgActionProcessor
    }

    fun createBrowserSaveNewElementAction(elementName: String, format: TGFileFormat): TGActionProcessorListener {
        val tgActionProcessor = this.createBrowserAction(TGBrowserSaveNewElementAction.NAME)
        tgActionProcessor.setAttribute(TGBrowserSaveElementAction.ATTRIBUTE_FORMAT, format)
        tgActionProcessor.setAttribute(TGBrowserSaveNewElementAction.ATTRIBUTE_NAME, elementName)
        return tgActionProcessor
    }

    fun createOpenSessionAction(collection: TGBrowserCollection): TGActionProcessorListener {
        val tgActionProcessor = this.createAction(TGBrowserOpenSessionAction.NAME)
        tgActionProcessor.setAttribute(TGBrowserOpenSessionAction.ATTRIBUTE_COLLECTION, collection)
        return tgActionProcessor
    }

    fun createCloseSessionAction(): TGActionProcessorListener {
        return this.createAction(TGBrowserCloseSessionAction.NAME)
    }

    fun createOpenDialogAction(controller: TGDialogController): TGActionProcessorListener {
        val tgActionProcessor = this.createAction(TGOpenDialogAction.NAME)
        tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.browser.findActivity())
        tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller)
        return tgActionProcessor
    }

    fun processConfirmableAction(actionProcessor: TGActionProcessor, confirmMessage: String) {
        val tgActionProcessor = this.createOpenDialogAction(TGConfirmDialogController())
        tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, confirmMessage)
        tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, Runnable { actionProcessor.process() })
        tgActionProcessor.process()
    }
}
