package app.tuxguitar.android.browser.model

import java.io.InputStream
import java.io.OutputStream

interface TGBrowserElement {
    fun getName(): String
    fun getParent(): TGBrowserElement?
    fun isFolder(): Boolean
    fun isWritable(): Boolean
    @Throws(TGBrowserException::class)
    fun getInputStream(): InputStream
    @Throws(TGBrowserException::class)
    fun getOutputStream(): OutputStream
}
