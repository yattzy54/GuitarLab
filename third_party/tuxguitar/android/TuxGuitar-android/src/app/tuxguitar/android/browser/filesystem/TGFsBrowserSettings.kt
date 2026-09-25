package app.tuxguitar.android.browser.filesystem

import app.tuxguitar.tools.browser.base.TGBrowserSettings

class TGFsBrowserSettings(val title: String, val path: String) {
    fun toBrowserSettings(): TGBrowserSettings = TGBrowserSettings().apply {
        this.title = this@TGFsBrowserSettings.title
        this.data = this@TGFsBrowserSettings.path
    }

    companion object {
        @JvmStatic
        fun createInstance(settings: TGBrowserSettings): TGFsBrowserSettings = TGFsBrowserSettings(settings.title, settings.data)
    }
}
