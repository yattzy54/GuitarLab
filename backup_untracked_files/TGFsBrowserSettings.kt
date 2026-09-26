package app.tuxguitar.android.browser.filesystem

import app.tuxguitar.tools.browser.base.TGBrowserSettings

class TGFsBrowserSettings(
    var title: String,
    var path: String,
) {
    fun toBrowserSettings(): TGBrowserSettings {
        val settings = TGBrowserSettings()
        settings.title = this.title
        settings.data = this.path
        return settings
    }

    companion object {
        @JvmStatic
        fun createInstance(settings: TGBrowserSettings): TGFsBrowserSettings =
            TGFsBrowserSettings(settings.title, settings.data)
    }
}
