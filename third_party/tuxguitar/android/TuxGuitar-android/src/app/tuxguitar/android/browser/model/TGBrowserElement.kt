package app.tuxguitar.android.browser.model

interface TGBrowserElement {
    fun getName(): String
    fun isFolder(): Boolean
    fun isWritable(): Boolean
}
