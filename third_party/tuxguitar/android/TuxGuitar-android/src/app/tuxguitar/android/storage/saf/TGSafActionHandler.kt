package app.tuxguitar.android.storage.saf

import android.content.Intent
import android.net.Uri
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.action.impl.gui.TGStartActivityForResultAction
import app.tuxguitar.android.action.impl.storage.uri.TGUriReadAction
import app.tuxguitar.android.action.impl.storage.uri.TGUriWriteAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.android.view.dialog.chooser.TGChooserDialogController
import app.tuxguitar.android.view.dialog.chooser.TGChooserDialogHandler
import app.tuxguitar.android.view.dialog.chooser.TGChooserDialogOption
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.file.TGWriteSongAction
import app.tuxguitar.io.base.TGFileFormat
import app.tuxguitar.util.TGContext

class TGSafActionHandler(private val context: TGContext) {
    fun createActionProcessor(id: String): TGActionProcessor {
        val tgActionProcessor = TGActionProcessor(context, id)
        tgActionProcessor.setAttribute(TGActivity::class.java.name, TGActivityController.getInstance(context).activity)
        return tgActionProcessor
    }

    fun callStartActivityForResult(intent: Intent, resultHandler: TGSafBaseHandler) {
        val processor = createActionProcessor(TGStartActivityForResultAction.NAME)
        processor.setAttribute(TGStartActivityForResultAction.ATTRIBUTE_INTENT, intent)
        processor.setAttribute(TGStartActivityForResultAction.ATTRIBUTE_REQUEST_CODE, resultHandler.requestCode)
        processor.process()
    }

    fun callReadUri(uri: Uri) {
        val processor = createActionProcessor(TGUriReadAction.NAME)
        processor.setAttribute(TGUriReadAction.ATTRIBUTE_URI, uri)
        processor.process()
    }

    fun callWriteUri(uri: Uri, fileFormat: TGFileFormat) {
        val processor = createActionProcessor(TGUriWriteAction.NAME)
        processor.setAttribute(TGUriWriteAction.ATTRIBUTE_URI, uri)
        processor.setAttribute(TGWriteSongAction.ATTRIBUTE_FORMAT, fileFormat)
        processor.process()
    }

    fun <T> callChooserDialog(title: String, options: List<TGChooserDialogOption<T>>, handler: TGChooserDialogHandler<T>) {
        val processor = createActionProcessor(TGOpenDialogAction.NAME)
        processor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, TGChooserDialogController<T>())
        processor.setAttribute(TGChooserDialogController.ATTRIBUTE_TITLE, title)
        processor.setAttribute(TGChooserDialogController.ATTRIBUTE_OPTIONS, options)
        processor.setAttribute(TGChooserDialogController.ATTRIBUTE_HANDLER, handler)
        processor.process()
    }
}
