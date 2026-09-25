package app.tuxguitar.android.browser.assets

import android.content.res.AssetManager
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.util.TGContext
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class TGAssetBrowserElement(
    private val context: TGContext,
    private val parentElement: TGAssetBrowserElement?,
    private val name: String
) : TGBrowserElement {
    override fun getName(): String = name
    override fun getParent(): TGAssetBrowserElement? = parentElement
    override fun isFolder(): Boolean = name.indexOf('.') == -1
    override fun isWritable(): Boolean = false
    @Throws(TGBrowserException::class)
    fun getChildreen(): List<TGBrowserElement> = findChildreen()
    @Throws(TGBrowserException::class)
    fun findChildreen(): List<TGBrowserElement> {
        return try {
            val elements = mutableListOf<TGBrowserElement>()
            val assetManager = findAssetManager()
            if (assetManager != null) {
                val assets = assetManager.list(fullPath)
                if (assets != null) {
                    for (asset in assets) elements.add(TGAssetBrowserElement(context, this, asset))
                }
            }
            elements
        } catch (e: IOException) {
            throw TGBrowserException()
        }
    }
    @Throws(TGBrowserException::class)
    override fun getInputStream(): InputStream {
        if (isFolder()) {
            throw TGBrowserException("Not a file")
        }
        return try {
            val assetManager = findAssetManager() ?: throw TGBrowserException("Asset manager not available")
            assetManager.open(fullPath)
        } catch (e: IOException) {
            throw TGBrowserException(e)
        }
    }
    @Throws(TGBrowserException::class)
    override fun getOutputStream(): OutputStream {
        throw TGBrowserException("No writable file system")
    }
    private val fullPath: String
        get() {
            var path = name
            var current = parentElement
            while (current != null) {
                path = current.getName() + File.separator + path
                current = current.getParent()
            }
            return path
        }
    private fun findAssetManager(): AssetManager? = TGActivityController.getInstance(context).activity?.assets
}
