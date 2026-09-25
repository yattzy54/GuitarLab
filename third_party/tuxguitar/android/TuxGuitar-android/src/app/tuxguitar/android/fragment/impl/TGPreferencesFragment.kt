package app.tuxguitar.android.fragment.impl

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.storage.TGStorageLoadSettingsAction
import app.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.properties.TGSharedPreferencesUtil
import app.tuxguitar.android.storage.TGStorageProperties
import app.tuxguitar.android.transport.TGTransportProperties
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.player.base.MidiOutputPort
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.util.TGContext

class TGPreferencesFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var updateActionsMap: MutableMap<String, String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = TGSharedPreferencesUtil.getSharedPreferencesName(activity, MODULE, RESOURCE)
        addPreferencesFromResource(R.xml.preferences_main)
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        createUpdateActionsMap()
        createSafPreferences()
        createOutputPortPreferences()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        val action = updateActionsMap?.get(key ?: return) ?: return
        TGActionProcessor(findContext(), action).process()
    }

    fun createUpdateActionsMap() {
        updateActionsMap = hashMapOf(
            TGTransportProperties.PROPERTY_MIDI_OUTPUT_PORT to TGTransportLoadSettingsAction.NAME,
            TGStorageProperties.PROPERTY_COLLECTION_BROWSER to TGStorageLoadSettingsAction.NAME,
        )
    }

    fun createSafPreferences() {
        val checkBoxPreference = findPreference<CheckBoxPreference>(TGStorageProperties.PROPERTY_COLLECTION_BROWSER)
        checkBoxPreference?.isChecked = TGStorageProperties(findContext()).isUseCollectionBrowser()
    }

    fun createOutputPortPreferences() {
        var currentValue: String? = null
        var currentLabel: String? = null
        val entryNames = ArrayList<String>()
        val entryValues = ArrayList<String>()

        val midiPlayer = MidiPlayer.getInstance(findContext())
        val outputPorts = midiPlayer.listOutputPorts()
        for (outputPort in outputPorts) {
            entryNames.add(outputPort.name)
            entryValues.add(outputPort.key)
            if (midiPlayer.isOutputPortOpen(outputPort.key)) {
                currentValue = outputPort.key
                currentLabel = outputPort.name
            }
        }

        val listPreference = findPreference<ListPreference>(TGTransportProperties.PROPERTY_MIDI_OUTPUT_PORT)
        listPreference?.entries = entryNames.toTypedArray<CharSequence>()
        listPreference?.entryValues = entryValues.toTypedArray<CharSequence>()
        if (currentValue != null) {
            listPreference?.value = currentValue
        }
        listPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, value ->
            val index = if (value != null) entryValues.indexOf(value.toString()) else -1
            val selectedLabel = if (index >= 0) entryNames[index] else null
            updatePreferenceSummary(preference, selectedLabel, R.string.preferences_midi_output_port_summary, R.string.preferences_midi_output_port_summary_empty)
            true
        }
        updatePreferenceSummary(listPreference, currentLabel, R.string.preferences_midi_output_port_summary, R.string.preferences_midi_output_port_summary_empty)
    }

    fun updatePreferenceSummary(preference: Preference?, label: String?, summaryId: Int, emptySummaryId: Int?) {
        if (label != null && label.isNotEmpty()) {
            preference?.summary = requireActivity().getString(summaryId, label)
        } else if (emptySummaryId != null) {
            preference?.summary = requireActivity().getString(emptySummaryId)
        }
    }

    fun findContext(): TGContext = (activity as TGActivity).findContext()

    companion object {
        const val MODULE = "tuxguitar"
        const val RESOURCE = "settings"
    }
}
