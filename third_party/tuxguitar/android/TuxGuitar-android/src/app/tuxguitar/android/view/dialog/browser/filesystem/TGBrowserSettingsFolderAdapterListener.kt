package app.tuxguitar.android.view.dialog.browser.filesystem

import java.io.File

fun interface TGBrowserSettingsFolderAdapterListener {
    fun onPathChanged(path: File?)
}
