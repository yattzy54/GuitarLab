package app.tuxguitar.android.browser.assets

import android.content.res.AssetManager
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.util.TGContext
import java.io.File
import java.io.IOException
import java.io.InputStream

class TGAssetBrowserElement(
    private val context: TGContext,
    val parent: TGAssetBrowserElement?,
    private val name: String,
) : TGBrowserElement {
    private var childreen: MutableList<TGBrowserElement>? = null

    override fun getName(): String = name

    override fun isFolder(): Boolean = !name.contains('.')

    override fun isWritable(): Boolean = false

    @Throws(TGBrowserException::class)
    fun getChildreen(): List<TGBrowserElement> {
        if (childreen == null) {
            childreen = findChildreen()
        }
        return childreen ?: emptyList()
    }

    @Throws(TGBrowserException::class)
    fun findChildreen(): MutableList<TGBrowserElement> {
        try {
            val elements = ArrayList<TGBrowserElement>()
            val assetManager = findAssetManager()
            if (assetManager != null) {
                val assets = assetManager.list(fullPath)
                if (assets != null) {
                    for (asset in assets) {
                        elements.add(TGAssetBrowserElement(context, this, asset))
                    }
                }
            }
            return elements
        } catch (e: IOException) {
            throw TGBrowserException()
        }
    }

    @Throws(TGBrowserException::class)
    fun getInputStream(): InputStream? {
        if (!isFolder()) {
            try {
                val assetManager = findAssetManager()
                if (assetManager != null) {
                    return assetManager.open(fullPath)
                }
            } catch (e: IOException) {
                throw TGBrowserException(e)
            }
        }
        return null
    }

    private val fullPath: String
        @Throws(TGBrowserException::class)
        get() {
            var path = name
            var currentParent = parent
            while (currentParent != null) {
                path = currentParent.name + File.separator + path
                currentParent = currentParent.parent
            }
            return path
        }

    private fun findAssetManager(): AssetManager? {
        val controller = TGActivityController.getInstance(context)
        return controller.activity?.assets
    }
}
