package app.tuxguitar.android.browser.assets

import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.tools.browser.base.TGBrowserSettings
import app.tuxguitar.util.TGContext

class TGAssetBrowserSettings(context: TGContext) {
    private val titleValue: String = TGActivityController.getInstance(context).activity?.getString(R.string.storage_saf_assets_provider_title) ?: "Assets"

    fun getId(): String = DEFAULT_ID
    fun getTitle(): String = titleValue
    fun getPath(): String = DEFAULT_PATH
    override fun equals(other: Any?): Boolean = other != null && this.hashCode() == other.hashCode()
    override fun hashCode(): Int = (TGAssetBrowserSettings::class.java.name + "-" + getId()).hashCode()
    fun toBrowserSettings(): TGBrowserSettings = TGBrowserSettings().apply {
        this.title = getTitle()
        this.data = getId()
    }

    companion object {
        private const val DEFAULT_ID = "browser-assets"
        private const val DEFAULT_PATH = "demo-songs"
    }
}
