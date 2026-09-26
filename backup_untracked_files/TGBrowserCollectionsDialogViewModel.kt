package app.tuxguitar.android.view.dialog.browser.collection

import app.tuxguitar.android.ui.state.EditorStateViewModel
import app.tuxguitar.tools.browser.TGBrowserCollection

data class TGBrowserCollectionsState(
    val collections: List<TGBrowserCollection> = emptyList(),
    val selectedFactoryIndex: Int = 0,
)

class TGBrowserCollectionsDialogViewModel :
    EditorStateViewModel<TGBrowserCollectionsState>(TGBrowserCollectionsState()) {
    fun setCollections(collections: List<TGBrowserCollection>) {
        update { it.copy(collections = collections.toList()) }
    }

    fun selectFactory(index: Int) {
        require(index >= 0) { "Factory index must not be negative" }
        update { it.copy(selectedFactoryIndex = index) }
    }
}
