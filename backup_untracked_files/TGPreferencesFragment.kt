package app.tuxguitar.android.fragment.impl

import android.content.SharedPreferences
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.tuxguitar.android.ui.state.editorViewModel
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.storage.TGStorageLoadSettingsAction
import app.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction
import app.tuxguitar.android.fragment.TGComposeCachedFragment
import app.tuxguitar.android.properties.TGSharedPreferencesUtil
import app.tuxguitar.android.storage.TGStorageProperties
import app.tuxguitar.android.transport.TGTransportProperties
import app.tuxguitar.android.view.preferences.TGPreferencesOutputPortOption
import app.tuxguitar.android.view.preferences.TGPreferencesScreen
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.player.base.MidiPlayer

class TGPreferencesFragment : TGComposeCachedFragment() {
    private lateinit var sharedPreferences: SharedPreferences

    private val viewModel by lazy { editorViewModel { TGPreferencesViewModel() } }

    override fun onPostCreate() {
        attachInstance()
        createActionBar(true, false, R.string.action_menu_settings)
        sharedPreferences = findActivity().requireContext().getSharedPreferences(
            TGSharedPreferencesUtil.getSharedPreferencesName(findActivity().requireContext(), MODULE, RESOURCE),
            0,
        )
        loadCollectionBrowserPreference()
        loadOutputPortPreferences()
    }

    fun attachInstance() {
        TGPreferencesFragmentController.getInstance(findContext()).attachInstance(this)
    }

    fun loadCollectionBrowserPreference() {
        viewModel.setCollectionBrowser(TGStorageProperties(findContext()).isUseCollectionBrowser())
    }

    fun loadOutputPortPreferences() {
        var currentValue: String? = null
        var currentLabel: String? = null
        val options = mutableListOf<TGPreferencesOutputPortOption>()

        val midiPlayer = MidiPlayer.getInstance(findContext())
        for (outputPort in midiPlayer.listOutputPorts()) {
            options.add(TGPreferencesOutputPortOption(outputPort.key, outputPort.name))
            if (midiPlayer.isOutputPortOpen(outputPort.key)) {
                currentValue = outputPort.key
                currentLabel = outputPort.name
            }
        }

        viewModel.setOutputPorts(options, currentValue, createOutputPortSummary(currentLabel))
    }

    fun createOutputPortSummary(label: String?): String =
        if (!label.isNullOrEmpty()) {
            findActivity().getString(R.string.preferences_midi_output_port_summary, label)
        } else {
            findActivity().getString(R.string.preferences_midi_output_port_summary_empty)
        }

    fun onUseCollectionBrowserChange(checked: Boolean) {
        viewModel.setCollectionBrowser(checked)
        sharedPreferences.edit().putBoolean(TGStorageProperties.PROPERTY_COLLECTION_BROWSER, checked).apply()
        TGActionProcessor(findContext(), TGStorageLoadSettingsAction.NAME).process()
    }

    fun onOutputPortSelected(option: TGPreferencesOutputPortOption) {
        viewModel.selectOutputPort(option, createOutputPortSummary(option.label))
        sharedPreferences.edit().putString(TGTransportProperties.PROPERTY_MIDI_OUTPUT_PORT, option.key).apply()
        TGActionProcessor(findContext(), TGTransportLoadSettingsAction.NAME).process()
    }

    @Composable
    override fun FragmentContent() {
        val state by viewModel.state.collectAsStateWithLifecycle()
        TGPreferencesScreen(
            useCollectionBrowser = state.useCollectionBrowser,
            onUseCollectionBrowserChange = ::onUseCollectionBrowserChange,
            outputPortOptions = state.outputPortOptions,
            selectedOutputPortKey = state.selectedOutputPortKey,
            onOutputPortSelected = ::onOutputPortSelected,
            generalCategoryTitle = getString(R.string.preferences_general_title),
            collectionBrowserTitle = getString(R.string.preferences_general_use_collection_browser_title),
            collectionBrowserSummary = getString(R.string.preferences_general_use_collection_browser_summary),
            soundCategoryTitle = getString(R.string.preferences_sound_category_title),
            outputPortTitle = getString(R.string.preferences_midi_output_port_title),
            outputPortSummary = state.outputPortSummary,
        )
    }

    companion object {
        const val MODULE = "tuxguitar"
        const val RESOURCE = "settings"
    }
}
