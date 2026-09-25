package app.tuxguitar.android.browser.filesystem

import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class TGFsBrowserElement(val file: File, private val parentElement: TGFsBrowserElement?) : TGBrowserElement {
    override fun getName(): String = file.name
    override fun isFolder(): Boolean = file.isDirectory
    override fun isWritable(): Boolean = file != null && if (file.exists()) file.canWrite() else parentElement?.isWritable() == true
    override fun getParent(): TGFsBrowserElement? = parentElement
    @Throws(TGBrowserException::class)
    override fun getInputStream(): InputStream = if (!isFolder()) {
        try { FileInputStream(file) } catch (e: Exception) { throw TGBrowserException(e.message ?: "", e) }
    } else {
        throw TGBrowserException("Not a file")
    }

    @Throws(TGBrowserException::class)
    override fun getOutputStream(): OutputStream = if (!isFolder()) {
        try { FileOutputStream(file) } catch (e: Exception) { throw TGBrowserException(e.message ?: "", e) }
    } else {
        throw TGBrowserException("Not a file")
    }
}
