package app.tuxguitar.android.browser.assets

import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.tools.browser.base.TGBrowserSettings
import app.tuxguitar.util.TGContext

class TGAssetBrowserSettings(context: TGContext) {
    val title: String = TGActivityController.getInstance(context).activity?.getString(R.string.storage_saf_assets_provider_title) ?: ""
    val id: String = DEFAULT_ID
    val path: String = DEFAULT_PATH

    fun toBrowserSettings(): TGBrowserSettings {
        val settings = TGBrowserSettings()
        settings.title = this.title
        settings.data = this.id
        return settings
    }

    override fun equals(other: Any?): Boolean = this.hashCode() == other?.hashCode()

    override fun hashCode(): Int = (TGAssetBrowserSettings::class.java.name + "-" + this.id).hashCode()

    companion object {
        private const val DEFAULT_ID = "browser-assets"
        private const val DEFAULT_PATH = "demo-songs"
    }
}
