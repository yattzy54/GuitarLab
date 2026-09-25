package app.tuxguitar.android.browser.assets

import app.tuxguitar.android.browser.model.TGBrowser
import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.browser.model.TGBrowserException
import app.tuxguitar.tools.browser.base.TGBrowserCallBack
import app.tuxguitar.util.TGContext
import java.io.InputStream
import java.io.OutputStream

class TGAssetBrowser(
    private val context: TGContext,
    private val data: TGAssetBrowserSettings,
) : TGBrowser {
    private var element: TGAssetBrowserElement? = null

    override fun open(callback: TGBrowserCallBack<Any>) {
        try {
            element = null
            callback.onSuccess(element)
        } catch (e: RuntimeException) {
            callback.handleError(e)
        }
    }

    override fun close(callback: TGBrowserCallBack<Any>) {
        try {
            element = null
            callback.onSuccess(element)
        } catch (e: RuntimeException) {
            callback.handleError(e)
        }
    }

    override fun cdElement(callback: TGBrowserCallBack<Any>, element: TGBrowserElement) {
        try {
            this.element = element as TGAssetBrowserElement
            callback.onSuccess(this.element)
        } catch (e: RuntimeException) {
            callback.handleError(e)
        }
    }

    override fun cdRoot(callback: TGBrowserCallBack<Any>) {
        try {
            element = TGAssetBrowserElement(context, null, data.path)
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
                elements.addAll(element!!.getChildreen())
                if (elements.isNotEmpty()) {
                    elements.sortWith(TGAssetBrowserElementComparator())
                }
            }
            callback.onSuccess(elements)
        } catch (e: Throwable) {
            callback.handleError(e)
        }
    }

    override fun createElement(callback: TGBrowserCallBack<TGBrowserElement>, name: String) {
        try {
            callback.onSuccess(null)
        } catch (e: RuntimeException) {
            callback.handleError(e)
        }
    }

    override fun getInputStream(callback: TGBrowserCallBack<InputStream>, element: TGBrowserElement) {
        try {
            callback.onSuccess((element as TGAssetBrowserElement).getInputStream())
        } catch (e: Throwable) {
            callback.handleError(e)
        }
    }

    override fun getOutputStream(callback: TGBrowserCallBack<OutputStream>, element: TGBrowserElement) {
        try {
            throw TGBrowserException("No writable file system")
        } catch (e: Throwable) {
            callback.handleError(e)
        }
    }

    override fun isWritable(): Boolean = false
}
