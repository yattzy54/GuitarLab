package app.tuxguitar.android.browser.filesystem

import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserException
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class TGFsBrowserElement(
    val file: File,
    val parent: TGFsBrowserElement?,
) : TGBrowserElement {
    override fun getName(): String = file.name

    override fun isFolder(): Boolean = file.isDirectory

    override fun isWritable(): Boolean = if (file.exists()) file.canWrite() else parent != null && parent.isWritable()

    @Throws(TGBrowserException::class)
    fun getInputStream(): InputStream? {
        if (!isFolder()) {
            try {
                return FileInputStream(file)
            } catch (e: FileNotFoundException) {
                throw TGBrowserException(e.message ?: "File not found", e)
            }
        }
        return null
    }

    @Throws(TGBrowserException::class)
    fun getOutputStream(): OutputStream? {
        if (!isFolder()) {
            try {
                return FileOutputStream(file)
            } catch (e: FileNotFoundException) {
                throw TGBrowserException(e.message ?: "File not found", e)
            }
        }
        return null
    }
}
