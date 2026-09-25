package app.tuxguitar.android.browser.filesystem

import app.tuxguitar.android.browser.model.TGBrowser
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserElementComparator
import app.tuxguitar.tools.browser.base.TGBrowserCallBack
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.TGExpressionResolver
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class TGFsBrowser(
    private val context: TGContext,
    private val data: TGFsBrowserSettings,
) : TGBrowser {
    private var element: TGFsBrowserElement? = null
    private var root: File? = null

    override fun open(callback: TGBrowserCallBack<Any>) {
        try {
            root = File(TGExpressionResolver.getInstance(context).resolve(data.path))
            element = null
            callback.onSuccess(element)
        } catch (e: RuntimeException) {
            callback.handleError(e)
        }
    }

    override fun close(callback: TGBrowserCallBack<Any>) {
        try {
            root = null
            element = null
            callback.onSuccess(element)
        } catch (e: RuntimeException) {
            callback.handleError(e)
        }
    }

    override fun cdElement(callback: TGBrowserCallBack<Any>, element: TGBrowserElement) {
        try {
            this.element = element as TGFsBrowserElement
            callback.onSuccess(this.element)
        } catch (e: RuntimeException) {
            callback.handleError(e)
        }
    }

    override fun cdRoot(callback: TGBrowserCallBack<Any>) {
        try {
            element = TGFsBrowserElement(root!!, null)
            callback.onSuccess(element)
        } catch (e: RuntimeException) {
            callback.handleError(e)
        }
    }

    override fun cdUp(callback: TGBrowserCallBack<Any>) {
        try {
            if (element != null && element!!.parent != null) {
                element = element!!.parent
            }
            callback.onSuccess(element)
        } catch (e: RuntimeException) {
            callback.handleError(e)
        }
    }

    override fun listElements(callback: TGBrowserCallBack<List<TGBrowserElement>>) {
        try {
            val elements = ArrayList<TGBrowserElement>()
            if (element != null) {
                val file = element!!.file
                if (file.exists() && file.isDirectory) {
                    val files = file.listFiles()
                    if (files != null) {
                        for (child in files) {
                            elements.add(TGFsBrowserElement(child, element))
                        }
                    }
                }
                if (elements.isNotEmpty()) {
                    elements.sortWith(TGBrowserElementComparator())
                }
            }
            callback.onSuccess(elements)
        } catch (e: Throwable) {
            callback.handleError(e)
        }
    }

    override fun createElement(callback: TGBrowserCallBack<TGBrowserElement>, name: String) {
        try {
            var element: TGBrowserElement? = null
            if (isWritable()) {
                val file = File(this.element!!.file, name)
                element = TGFsBrowserElement(file, this.element)
            }
            callback.onSuccess(element)
        } catch (e: Throwable) {
            callback.handleError(e)
        }
    }

    override fun getInputStream(callback: TGBrowserCallBack<InputStream>, element: TGBrowserElement) {
        try {
            callback.onSuccess((element as TGFsBrowserElement).getInputStream())
        } catch (e: Throwable) {
            callback.handleError(e)
        }
    }

    override fun getOutputStream(callback: TGBrowserCallBack<OutputStream>, element: TGBrowserElement) {
        try {
            callback.onSuccess((element as TGFsBrowserElement).getOutputStream())
        } catch (e: Throwable) {
            callback.handleError(e)
        }
    }

    override fun isWritable(): Boolean = element != null && element!!.isFolder() && element!!.isWritable()
}
