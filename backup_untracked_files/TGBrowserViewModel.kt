package app.tuxguitar.android.fragment.impl

import app.tuxguitar.android.browser.model.TGBrowserElement
import app.tuxguitar.android.ui.state.EditorStateViewModel
import app.tuxguitar.android.view.util.TGSelectableItem

data class TGBrowserState(
    val elements: List<TGBrowserElement> = emptyList(),
    val collectionOptions: List<TGSelectableItem> = emptyList(),
    val formatOptions: List<TGSelectableItem> = emptyList(),
    val selectedCollection: TGSelectableItem? = null,
    val selectedFormat: TGSelectableItem? = null,
    val saveElementName: String = "",
    val showSavePanel: Boolean = false,
    val saveControlsEnabled: Boolean = false,
)

class TGBrowserViewModel : EditorStateViewModel<TGBrowserState>(TGBrowserState()) {
    fun updateElements(elements: List<TGBrowserElement>) {
        update { it.copy(elements = elements.toList()) }
    }

    fun updateCollections(options: List<TGSelectableItem>, selected: Any?) {
        update {
            it.copy(
                collectionOptions = options.toList(),
                selectedCollection = options.firstOrNull { option -> option.getItem() == selected } ?: options.firstOrNull(),
            )
        }
    }

    fun updateFormats(options: List<TGSelectableItem>) {
        update {
            it.copy(
                formatOptions = options.toList(),
                selectedFormat = options.firstOrNull { option -> option.getItem() == it.selectedFormat?.getItem() }
                    ?: options.firstOrNull(),
            )
        }
    }

    fun selectCollection(item: TGSelectableItem) {
        update { it.copy(selectedCollection = item) }
    }

    fun selectFormat(item: TGSelectableItem) {
        update { it.copy(selectedFormat = item) }
    }

    fun setSaveName(value: String) {
        update { it.copy(saveElementName = value) }
    }

    fun updateSavePanel(visible: Boolean, writable: Boolean = state.value.saveControlsEnabled, defaultName: String? = null) {
        update {
            it.copy(
                showSavePanel = visible,
                saveControlsEnabled = writable,
                saveElementName = defaultName ?: it.saveElementName,
            )
        }
    }
}
