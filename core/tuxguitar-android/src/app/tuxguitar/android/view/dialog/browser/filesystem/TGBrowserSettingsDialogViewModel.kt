package app.tuxguitar.android.view.dialog.browser.filesystem

import app.tuxguitar.android.ui.state.EditorStateViewModel
import java.io.File

data class TGBrowserSettingsState(val name: String, val path: File?)

class TGBrowserSettingsDialogViewModel(initial: TGBrowserSettingsState) :
    EditorStateViewModel<TGBrowserSettingsState>(initial) {
    fun setName(name: String) {
        update { it.copy(name = name) }
    }

    fun selectFolder(path: File) {
        update { it.copy(path = path) }
    }

    fun validate(): TGBrowserSettingsError? {
        val current = state.value
        return when {
            current.name.isEmpty() -> TGBrowserSettingsError.EMPTY_NAME
            current.path == null -> TGBrowserSettingsError.EMPTY_PATH
            !current.path.exists() -> TGBrowserSettingsError.NONEXISTENT_PATH
            !current.path.isDirectory -> TGBrowserSettingsError.NONFOLDER_PATH
            else -> null
        }
    }
}

enum class TGBrowserSettingsError { EMPTY_NAME, EMPTY_PATH, NONEXISTENT_PATH, NONFOLDER_PATH }
