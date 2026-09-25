package app.tuxguitar.android.view.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A single selectable MIDI output port option, mirroring the
 * (label, key) pairs the old [androidx.preference.ListPreference]
 * populated its entries/entryValues arrays with.
 */
data class TGPreferencesOutputPortOption(
    val key: String,
    val label: String,
)

@Composable
fun TGPreferencesScreen(
    useCollectionBrowser: Boolean,
    onUseCollectionBrowserChange: (Boolean) -> Unit,
    outputPortOptions: List<TGPreferencesOutputPortOption>,
    selectedOutputPortKey: String?,
    onOutputPortSelected: (TGPreferencesOutputPortOption) -> Unit,
    generalCategoryTitle: String,
    collectionBrowserTitle: String,
    collectionBrowserSummary: String,
    soundCategoryTitle: String,
    outputPortTitle: String,
    outputPortSummary: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TGPreferencesCategory(title = generalCategoryTitle) {
            TGPreferencesSwitchItem(
                title = collectionBrowserTitle,
                summary = collectionBrowserSummary,
                checked = useCollectionBrowser,
                onCheckedChange = onUseCollectionBrowserChange,
            )
        }
        TGPreferencesCategory(title = soundCategoryTitle) {
            TGPreferencesDropdownItem(
                title = outputPortTitle,
                summary = outputPortSummary,
                options = outputPortOptions,
                selectedKey = selectedOutputPortKey,
                onSelected = onOutputPortSelected,
            )
        }
    }
}

@Composable
private fun TGPreferencesCategory(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp),
        )
        content()
    }
}

@Composable
private fun TGPreferencesSwitchItem(
    title: String,
    summary: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = summary, style = MaterialTheme.typography.bodyMedium)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
    HorizontalDivider()
}

@Composable
private fun TGPreferencesDropdownItem(
    title: String,
    summary: String,
    options: List<TGPreferencesOutputPortOption>,
    selectedKey: String?,
    onSelected: (TGPreferencesOutputPortOption) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = options.isNotEmpty()) { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = summary, style = MaterialTheme.typography.bodyMedium)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 320.dp),
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            fontWeight = if (option.key == selectedKey) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelected(option)
                    },
                )
            }
        }
    }
    HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
private fun TGPreferencesScreenPreview() {
    MaterialTheme {
        TGPreferencesScreen(
            useCollectionBrowser = true,
            onUseCollectionBrowserChange = {},
            outputPortOptions = listOf(
                TGPreferencesOutputPortOption("port-1", "Built-in synth"),
                TGPreferencesOutputPortOption("port-2", "USB MIDI device"),
            ),
            selectedOutputPortKey = "port-1",
            onOutputPortSelected = {},
            generalCategoryTitle = "General",
            collectionBrowserTitle = "Collection Browser",
            collectionBrowserSummary = "Use Collection Browser to browse files",
            soundCategoryTitle = "Sound",
            outputPortTitle = "MIDI Port",
            outputPortSummary = "Built-in synth",
        )
    }
}
