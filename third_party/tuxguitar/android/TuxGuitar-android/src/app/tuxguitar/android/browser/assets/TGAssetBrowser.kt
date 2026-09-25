package app.tuxguitar.android.browser.assets

import app.tuxguitar.android.browser.model.TGBrowser
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.tools.browser.base.TGBrowserCallBack
import app.tuxguitar.util.TGContext
import java.io.InputStream
import java.io.OutputStream

class TGAssetBrowser(private val context: TGContext, private val data: TGAssetBrowserSettings) : TGBrowser {
    private var element: TGAssetBrowserElement? = null

    override fun open(cb: TGBrowserCallBack<Any>) {
        try {
            element = null
            cb.onSuccess(null)
        } catch (e: RuntimeException) { cb.handleError(e) }
    }
    override fun close(cb: TGBrowserCallBack<Any>) {
        try {
            element = null
            cb.onSuccess(null)
        } catch (e: RuntimeException) { cb.handleError(e) }
    }
    override fun cdElement(cb: TGBrowserCallBack<Any>, element: TGBrowserElement) {
        try {
            this.element = element as TGAssetBrowserElement
            cb.onSuccess(this.element)
        } catch (e: RuntimeException) { cb.handleError(e) }
    }
    override fun cdRoot(cb: TGBrowserCallBack<Any>) {
        try {
            element = TGAssetBrowserElement(context, null, data.getPath())
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
            if (element != null) {
                elements.addAll(element!!.getChildreen())
                if (elements.isNotEmpty()) elements.sortWith(TGAssetBrowserElementComparator())
            }
            cb.onSuccess(elements)
        } catch (e: Throwable) { cb.handleError(e) }
    }
    override fun createElement(cb: TGBrowserCallBack<TGBrowserElement>, name: String) { try { cb.onSuccess(null) } catch (e: RuntimeException) { cb.handleError(e) } }
    override fun getInputStream(cb: TGBrowserCallBack<InputStream>, element: TGBrowserElement) {
        try { cb.onSuccess((element as TGAssetBrowserElement).getInputStream()) } catch (e: Throwable) { cb.handleError(e) }
    }
    override fun getOutputStream(cb: TGBrowserCallBack<OutputStream>, element: TGBrowserElement) {
        try { throw TGBrowserException("No writable file system") } catch (e: Throwable) { cb.handleError(e) }
    }
    override fun isWritable(): Boolean = false
}
