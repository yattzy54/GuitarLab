package app.tuxguitar.android.storage.saf

import android.content.Intent
import android.net.Uri
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.android.storage.TGStorageProvider
import app.tuxguitar.android.view.dialog.chooser.TGChooserDialogHandler
import app.tuxguitar.android.view.dialog.chooser.TGChooserDialogOption
import app.tuxguitar.io.base.TGFileFormat
import app.tuxguitar.io.base.TGFileFormatManager
import app.tuxguitar.io.base.TGFileFormatUtils
import app.tuxguitar.util.TGAbstractContext
import app.tuxguitar.util.TGContext

class TGSafProvider(private val context: TGContext) : TGStorageProvider {
    private val session: TGSafSession = TGSafSession()
    private val actionHandler: TGSafActionHandler = TGSafActionHandler(context)

    fun getActivity(): TGActivity = TGActivityController.getInstance(context).activity
        ?: throw IllegalStateException("TGSafProvider requires an active TGActivity")

    override fun updateSession(source: TGAbstractContext) {
        session.uri = source.getAttribute<Uri>(Uri::class.java.name)
        session.fileFormat = source.getAttribute<TGFileFormat>(TGFileFormat::class.java.name)
    }

    override fun openDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = MIME_TYPE
        intent.putExtra(EXTRA_SHOW_ADVANCED, true)
        actionHandler.callStartActivityForResult(intent, TGSafOpenHandler(this))
    }

    fun saveDocumentAs(fileFormat: TGFileFormat) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = MIME_TYPE
        intent.putExtra(EXTRA_SHOW_ADVANCED, true)
        intent.putExtra(Intent.EXTRA_TITLE, createDefaultFileName(fileFormat))
        actionHandler.callStartActivityForResult(intent, TGSafSaveHandler(this, fileFormat))
    }

    override fun saveDocumentAs() {
        val fileFormatManager = TGFileFormatManager.getInstance(context)
        val options = ArrayList<TGChooserDialogOption<TGFileFormat>>()

        val commonFormats = fileFormatManager.findWriteFileFormats(true)
        for (format in commonFormats) {
            options.add(TGChooserDialogOption(format.name, format))
        }

        val nonCommonFormats = fileFormatManager.findWriteFileFormats(false)
        for (format in nonCommonFormats) {
            options.add(TGChooserDialogOption(getActivity().getString(R.string.storage_export_to, format.name), format))
        }

        if (options.size == 1) {
            saveDocumentAs(options[0].value)
        } else {
            val title = getActivity().getString(R.string.storage_saf_file_format_chooser_title)
            actionHandler.callChooserDialog(title, options, object : TGChooserDialogHandler<TGFileFormat> {
                override fun onChoose(value: TGFileFormat?) {
                    if (value != null) {
                        saveDocumentAs(value)
                    }
                }
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

    fun getActionHandler(): TGSafActionHandler = actionHandler

    fun getSession(): TGSafSession = session

    fun getContext(): TGContext = context

    companion object {
        private const val MIME_TYPE = "*/*"
        private const val EXTRA_SHOW_ADVANCED = "android.content.extra.SHOW_ADVANCED"
    }
}
