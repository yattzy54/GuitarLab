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

class TGFsBrowser(private val context: TGContext, private val data: TGFsBrowserSettings) : TGBrowser {
    private var root: File? = null
    private var element: TGFsBrowserElement? = null

    override fun open(cb: TGBrowserCallBack<Any>) {
        try {
            root = File(TGExpressionResolver.getInstance(context).resolve(data.path))
            element = null
            cb.onSuccess(null)
        } catch (e: RuntimeException) { cb.handleError(e) }
    }

    override fun close(cb: TGBrowserCallBack<Any>) {
        try {
            root = null
            element = null
            cb.onSuccess(null)
        } catch (e: RuntimeException) { cb.handleError(e) }
    }

    override fun cdElement(cb: TGBrowserCallBack<Any>, element: TGBrowserElement) {
        try {
            this.element = element as TGFsBrowserElement
            cb.onSuccess(this.element)
        } catch (e: RuntimeException) { cb.handleError(e) }
    }

    override fun cdRoot(cb: TGBrowserCallBack<Any>) {
        try {
            element = TGFsBrowserElement(root!!, null)
            cb.onSuccess(element)
        } catch (e: RuntimeException) { cb.handleError(e) }
    }

    override fun cdUp(cb: TGBrowserCallBack<Any>) {
        try {
            if (element != null && element!!.getParent() != null) element = element!!.getParent()
            cb.onSuccess(element)
        } catch (e: RuntimeException) { cb.handleError(e) }
    }

    override fun listElements(cb: TGBrowserCallBack<List<TGBrowserElement>>) {
        try {
            val elements = mutableListOf<TGBrowserElement>()
            val current = element
            if (current != null) {
                val file = current.file
                if (file.exists() && file.isDirectory) {
                    val files = file.listFiles()
                    if (files != null) {
                        for (child in files) elements.add(TGFsBrowserElement(child, current))
                    }
                }
                if (elements.isNotEmpty()) elements.sortWith(TGBrowserElementComparator())
            }
            cb.onSuccess(elements)
        } catch (e: Throwable) { cb.handleError(e) }
    }

    override fun createElement(cb: TGBrowserCallBack<TGBrowserElement>, name: String) {
        try {
            val created = if (isWritable()) {
                val file = File(element!!.file, name)
                TGFsBrowserElement(file, element)
            } else null
            cb.onSuccess(created)
        } catch (e: Throwable) { cb.handleError(e) }
    }

    override fun getInputStream(cb: TGBrowserCallBack<InputStream>, element: TGBrowserElement) {
        try { cb.onSuccess((element as TGFsBrowserElement).getInputStream()) } catch (e: Throwable) { cb.handleError(e) }
    }

    override fun getOutputStream(cb: TGBrowserCallBack<OutputStream>, element: TGBrowserElement) {
        try { cb.onSuccess((element as TGFsBrowserElement).getOutputStream()) } catch (e: Throwable) { cb.handleError(e) }
    }

    override fun isWritable(): Boolean = element != null && element!!.isFolder() && element!!.isWritable()
}
