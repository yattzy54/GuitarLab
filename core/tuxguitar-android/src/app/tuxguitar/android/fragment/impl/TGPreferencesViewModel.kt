package app.tuxguitar.android.fragment.impl

import app.tuxguitar.android.ui.state.EditorStateViewModel
import app.tuxguitar.android.view.preferences.TGPreferencesOutputPortOption

data class TGPreferencesState(
    val useCollectionBrowser: Boolean = false,
    val outputPortOptions: List<TGPreferencesOutputPortOption> = emptyList(),
    val selectedOutputPortKey: String? = null,
    val outputPortSummary: String = "",
)

class TGPreferencesViewModel : EditorStateViewModel<TGPreferencesState>(TGPreferencesState()) {
    fun setCollectionBrowser(enabled: Boolean) {
        update { it.copy(useCollectionBrowser = enabled) }
    }

    fun setOutputPorts(options: List<TGPreferencesOutputPortOption>, selectedKey: String?, summary: String) {
        update { it.copy(outputPortOptions = options.toList(), selectedOutputPortKey = selectedKey, outputPortSummary = summary) }
    }

    fun selectOutputPort(option: TGPreferencesOutputPortOption, summary: String) {
        require(option in state.value.outputPortOptions) { "Unknown MIDI output port: ${option.key}" }
        update { it.copy(selectedOutputPortKey = option.key, outputPortSummary = summary) }
    }
}
