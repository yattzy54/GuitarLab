package app.tuxguitar.android.storage.saf.assets

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.database.Cursor
import android.database.MatrixCursor
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.provider.DocumentsProvider
import app.tuxguitar.android.R
import app.tuxguitar.android.util.TGStreamUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

@SuppressLint("NewApi")
class TGAssetsProvider : DocumentsProvider() {
    private var assets: AssetManager? = null

    @Throws(FileNotFoundException::class)
    override fun queryRoots(projection: Array<String>?): Cursor {
        val result = MatrixCursor(resolveProjection(projection, DEFAULT_ROOT_PROJECTION))
        val row = result.newRow()
        val appName = context?.getString(R.string.app_name) ?: ""
        val providerTitle = context?.getString(R.string.storage_saf_assets_provider_title) ?: ""
        row.add(DocumentsContract.Root.COLUMN_ROOT_ID, ROOT_ID)
        row.add(DocumentsContract.Root.COLUMN_FLAGS, DocumentsContract.Root.FLAG_SUPPORTS_RECENTS or DocumentsContract.Root.FLAG_SUPPORTS_SEARCH)
        row.add(DocumentsContract.Root.COLUMN_TITLE, appName)
        row.add(DocumentsContract.Root.COLUMN_SUMMARY, providerTitle)
        row.add(DocumentsContract.Root.COLUMN_DOCUMENT_ID, ROOT_ID)
        row.add(DocumentsContract.Root.COLUMN_ICON, R.drawable.ic_launcher)
        return result
    }

    @Throws(FileNotFoundException::class)
    override fun queryChildDocuments(parentDocumentId: String?, projection: Array<String>?, sortOrder: String?): Cursor {
        try {
            val result = MatrixCursor(resolveProjection(projection, DEFAULT_DOCUMENT_PROJECTION))
            if (assets != null) {
                val parentId = parentDocumentId ?: ROOT_ID
                val childAssets = assets!!.list(parentId)
                if (childAssets != null) {
                    for (asset in childAssets) {
                        createFileRow(result, parentId, asset)
                    }
                }
            }
            return result
        } catch (e: IOException) {
            e.printStackTrace()
            throw FileNotFoundException()
        }
    }

    @Throws(FileNotFoundException::class)
    override fun queryDocument(documentId: String?, projection: Array<String>?): Cursor {
        val result = MatrixCursor(resolveProjection(projection, DEFAULT_DOCUMENT_PROJECTION))
        createFileRow(result, documentId)
        return result
    }

    @Throws(FileNotFoundException::class)
    override fun openDocument(documentId: String?, mode: String?, cancellationSignal: CancellationSignal?): ParcelFileDescriptor {
        try {
            val pipe = ParcelFileDescriptor.createPipe()
            val assetId = documentId ?: throw FileNotFoundException()
            if (assets != null) {
                TGStreamUtil.write(assets!!.open(assetId), ParcelFileDescriptor.AutoCloseOutputStream(pipe[1]))
            }
            return pipe[0]
        } catch (e: IOException) {
            e.printStackTrace()
            throw FileNotFoundException()
        }
    }

    override fun onCreate(): Boolean {
        assets = context?.assets
        return assets != null
    }

    fun resolveProjection(projection: Array<String>?, defaults: Array<String>): Array<String> = projection ?: defaults

    fun createFileRow(result: MatrixCursor, parent: String?, asset: String) {
        val normalizedParent = parent ?: ""
        createFileRow(result, normalizedParent + File.separator + asset)
    }

    fun createFileRow(result: MatrixCursor, documentId: String?) {
        val row = result.newRow()
        row.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, documentId ?: "")
        row.add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, getDocumentName(documentId))
        row.add(DocumentsContract.Document.COLUMN_MIME_TYPE, getMimeType(documentId))
    }

    fun getDocumentName(documentId: String?): String {
        val paths = documentId?.split(File.separator.toRegex())?.toTypedArray() ?: emptyArray()
        return if (paths.isNotEmpty()) paths[paths.size - 1] else documentId ?: ""
    }

    fun getMimeType(documentId: String?): String = if (isDirectory(documentId)) DocumentsContract.Document.MIME_TYPE_DIR else "*/*"

    fun isDirectory(documentId: String?): Boolean = getDocumentName(documentId).indexOf('.') == -1

    companion object {
        private const val ROOT_ID = "demo-songs"
        private val DEFAULT_ROOT_PROJECTION = arrayOf(
            DocumentsContract.Root.COLUMN_ROOT_ID,
            DocumentsContract.Root.COLUMN_FLAGS,
            DocumentsContract.Root.COLUMN_TITLE,
            DocumentsContract.Root.COLUMN_SUMMARY,
            DocumentsContract.Root.COLUMN_DOCUMENT_ID,
            DocumentsContract.Root.COLUMN_ICON,
        )
        private val DEFAULT_DOCUMENT_PROJECTION = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
        )
    }
}
