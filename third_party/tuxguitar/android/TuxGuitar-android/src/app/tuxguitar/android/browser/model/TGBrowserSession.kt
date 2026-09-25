package app.tuxguitar.android.browser.model

import app.tuxguitar.io.base.TGFileFormat
import app.tuxguitar.tools.browser.TGBrowserCollection

class TGBrowserSession {
    var sessionType: Int = READ_MODE
    var browser: TGBrowser? = null
    var collection: TGBrowserCollection? = null
    var currentElements: List<TGBrowserElement>? = null
    var currentElement: TGBrowserElement? = null
    var currentFormat: TGFileFormat? = null

    companion object {
        const val READ_MODE = 1
        const val WRITE_MODE = 2
    }
}
