package app.tuxguitar.android.view.channel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TGChannelListItemContent(
    name: String,
    volume: Int,
    onVolumeChange: (Int) -> Unit,
    onClick: () -> Unit,
    isRemovable: Boolean = true,
    onRemove: (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    var sliderValue by remember(volume) { mutableFloatStateOf(volume.toFloat()) }

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = onClick, onLongClick = { expanded = true })
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(text = name)
            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                    onVolumeChange(it.toInt())
                },
                valueRange = 0f..127f,
            )
        }
        HorizontalDivider()
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.action_channel_list_item_edit)) },
                onClick = {
                    expanded = false
                    onClick()
                },
            )
            if (isRemovable && onRemove != null) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_channel_list_item_remove)) },
                    onClick = {
                        expanded = false
                        onRemove()
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGChannelListItemContentPreview() {
    androidx.compose.material3.MaterialTheme {
        Column {
            TGChannelListItemContent(
                name = "Distortion Guitar",
                volume = 96,
                onVolumeChange = {},
                onClick = {},
                isRemovable = true,
                onRemove = {},
            )
            TGChannelListItemContent(
                name = "Drums",
                volume = 110,
                onVolumeChange = {},
                onClick = {},
                isRemovable = false,
            )
        }
    }
}
