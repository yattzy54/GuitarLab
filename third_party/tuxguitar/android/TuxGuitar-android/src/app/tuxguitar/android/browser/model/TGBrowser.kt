package app.tuxguitar.android.browser.model

import app.tuxguitar.tools.browser.base.TGBrowserCallBack
import java.io.InputStream
import java.io.OutputStream

interface TGBrowser {
    fun open(callback: TGBrowserCallBack<Any>)
    fun close(callback: TGBrowserCallBack<Any>)
    fun cdRoot(callback: TGBrowserCallBack<Any>)
    fun cdUp(callback: TGBrowserCallBack<Any>)
    fun cdElement(callback: TGBrowserCallBack<Any>, element: TGBrowserElement)
    fun listElements(callback: TGBrowserCallBack<List<TGBrowserElement>>)
    fun createElement(callback: TGBrowserCallBack<TGBrowserElement>, name: String)
    fun getInputStream(callback: TGBrowserCallBack<InputStream>, element: TGBrowserElement)
    fun getOutputStream(callback: TGBrowserCallBack<OutputStream>, element: TGBrowserElement)
    fun isWritable(): Boolean
}
