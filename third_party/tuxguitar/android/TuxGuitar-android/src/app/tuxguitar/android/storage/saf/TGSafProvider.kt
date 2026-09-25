package app.tuxguitar.android.storage.saf

import android.content.Intent
import android.net.Uri
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.android.storage.TGStorageProvider
import app.tuxguitar.android.view.dialog.chooser.TGChooserDialogController
import app.tuxguitar.android.view.dialog.chooser.TGChooserDialogHandler
import app.tuxguitar.android.view.dialog.chooser.TGChooserDialogOption
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.file.TGWriteSongAction
import app.tuxguitar.io.base.TGFileFormat
import app.tuxguitar.io.base.TGFileFormatManager
import app.tuxguitar.io.base.TGFileFormatUtils
import app.tuxguitar.util.TGAbstractContext
import app.tuxguitar.util.TGContext

class TGSafProvider(val context: TGContext) : TGStorageProvider {
    val session = TGSafSession()
    private val actionHandler = TGSafActionHandler(context)

    override fun updateSession(source: TGAbstractContext) {
        session.uri = source.getAttribute(Uri::class.java.name) as? Uri
        session.fileFormat = source.getAttribute(TGFileFormat::class.java.name) as? TGFileFormat
    }

    override fun openDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = MIME_TYPE
            putExtra(EXTRA_SHOW_ADVANCED, true)
        }
        actionHandler.callStartActivityForResult(intent, TGSafOpenHandler(this))
    }

    fun saveDocumentAs(fileFormat: TGFileFormat) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = MIME_TYPE
            putExtra(EXTRA_SHOW_ADVANCED, true)
            putExtra(Intent.EXTRA_TITLE, createDefaultFileName(fileFormat))
        }
        actionHandler.callStartActivityForResult(intent, TGSafSaveHandler(this, fileFormat))
    }

    override fun saveDocumentAs() {
        val fileFormatManager = TGFileFormatManager.getInstance(context)
        val options = mutableListOf<TGChooserDialogOption<TGFileFormat>>()
        for (format in fileFormatManager.findWriteFileFormats(true)) {
            options.add(TGChooserDialogOption(format.name, format))
        }
        for (format in fileFormatManager.findWriteFileFormats(false)) {
            options.add(TGChooserDialogOption(getActivity().getString(R.string.storage_export_to, format.name), format))
        }
        if (options.size == 1) {
            saveDocumentAs(options[0].value)
        } else {
            val title = getActivity().getString(R.string.storage_saf_file_format_chooser_title)
            actionHandler.callChooserDialog(title, options, object : TGChooserDialogHandler<TGFileFormat> {
                override fun onChoose(value: TGFileFormat?) { if (value != null) saveDocumentAs(value) }
            })
        }
    }

    override fun saveDocument() {
        if (session.uri != null && session.fileFormat != null) {
            actionHandler.callWriteUri(session.uri!!, session.fileFormat!!)
        } else {
            saveDocumentAs()
        }
    }

    fun createDefaultFileName(format: TGFileFormat): String {
        val prefix = getActivity().getString(R.string.storage_default_filename)
        val suffix = TGFileFormatUtils.getDefaultExtension(format)
        return prefix + suffix
    }

    fun getActivity(): TGActivity = TGActivityController.getInstance(context).activity!!
    fun getActionHandler(): TGSafActionHandler = actionHandler

    companion object {
        private const val MIME_TYPE = "*/*"
        private const val EXTRA_SHOW_ADVANCED = "android.content.extra.SHOW_ADVANCED"
    }
}
