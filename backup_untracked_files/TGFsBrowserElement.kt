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

    override fun getParent(): TGBrowserElement? = parent

    override fun isFolder(): Boolean = file.isDirectory

    override fun isWritable(): Boolean = if (file.exists()) file.canWrite() else parent != null && parent.isWritable()

    @Throws(TGBrowserException::class)
    override fun getInputStream(): InputStream {
        if (isFolder()) {
            throw TGBrowserException("Folder cannot be opened as a file")
        }
        return try {
            FileInputStream(file)
        } catch (e: FileNotFoundException) {
            throw TGBrowserException(e.message ?: "File not found", e)
        }
    }

    @Throws(TGBrowserException::class)
    override fun getOutputStream(): OutputStream {
        if (isFolder()) {
            throw TGBrowserException("Folder cannot be opened for writing")
        }
        return try {
            FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            throw TGBrowserException(e.message ?: "File not found", e)
        }
    }
}
