package app.tuxguitar.android.view.browser

import android.content.Context
import android.content.res.TypedArray
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.util.TGSelectableItem

@Composable
fun TGBrowserScreen(
    collections: List<TGSelectableItem>,
    selectedCollection: TGSelectableItem?,
    onCollectionSelected: (TGSelectableItem) -> Unit,
    elementsContent: @Composable () -> Unit,
    showSavePanel: Boolean,
    saveElementName: String,
    onSaveElementNameChange: (String) -> Unit,
    formatOptions: List<TGSelectableItem>,
    selectedFormat: TGSelectableItem?,
    onFormatSelected: (TGSelectableItem) -> Unit,
    onSaveClick: () -> Unit,
    saveControlsEnabled: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TGBrowserDropdownField(
            selectedOption = selectedCollection,
            options = collections,
            onSelected = onCollectionSelected,
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            elementsContent()
        }
        if (showSavePanel) {
            TGBrowserSavePanel(
                saveElementName = saveElementName,
                onSaveElementNameChange = onSaveElementNameChange,
                formatOptions = formatOptions,
                selectedFormat = selectedFormat,
                onFormatSelected = onFormatSelected,
                onSaveClick = onSaveClick,
                saveControlsEnabled = saveControlsEnabled,
            )
        }
    }
}

@Composable
private fun TGBrowserDropdownField(
    selectedOption: TGSelectableItem?,
    options: List<TGSelectableItem>,
    onSelected: (TGSelectableItem) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedOption?.getLabel().orEmpty(),
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .let {
                    if (enabled) {
                        it.clickable { expanded = true }
                    } else {
                        it
                    }
                },
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 320.dp),
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.getDropDownLabel().orEmpty()) },
                    onClick = {
                        expanded = false
                        onSelected(option)
                    },
                )
            }
        }
    }
}

@Composable
private fun TGBrowserSavePanel(
    saveElementName: String,
    onSaveElementNameChange: (String) -> Unit,
    formatOptions: List<TGSelectableItem>,
    selectedFormat: TGSelectableItem?,
    onFormatSelected: (TGSelectableItem) -> Unit,
    onSaveClick: () -> Unit,
    saveControlsEnabled: Boolean,
) {
    val saveIconResId = rememberBrowserSaveIconResId()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = saveElementName,
            onValueChange = onSaveElementNameChange,
            enabled = saveControlsEnabled,
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        Box(modifier = Modifier.weight(1f)) {
            TGBrowserDropdownField(
                selectedOption = selectedFormat,
                options = formatOptions,
                onSelected = onFormatSelected,
                enabled = saveControlsEnabled,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        IconButton(
            onClick = onSaveClick,
            enabled = saveControlsEnabled,
        ) {
            Icon(
                painter = painterResource(saveIconResId),
                contentDescription = stringResource(R.string.browser_save_label),
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
private fun rememberBrowserSaveIconResId(): Int {
    val context = LocalContext.current
    return remember(context) {
        context.findStyledDrawableResource(R.style.TGImageButton_Save)
    }
}

private fun Context.findStyledDrawableResource(styleResId: Int): Int {
    val typedArray: TypedArray = obtainStyledAttributes(styleResId, intArrayOf(android.R.attr.src))
    try {
        return typedArray.getResourceId(0, 0)
    } finally {
        typedArray.recycle()
    }
}
