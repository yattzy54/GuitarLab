package app.tuxguitar.android.view.dialog.chooser

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog

class TGChooserDialog<T> : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val handler = requireNotNull(getHandler())
        val options = requireNotNull(getOptions())
        TGChooserDialogContent(
            title = getTitle(),
            options = options,
            onChoose = { option ->
                onChooseInNewThread(handler, option.value)
                onDismiss()
            },
        )
    }

    fun getTitle(): String? = getAttribute(TGChooserDialogController.ATTRIBUTE_TITLE)

    fun getHandler(): TGChooserDialogHandler<T>? =
        getAttribute(TGChooserDialogController.ATTRIBUTE_HANDLER)

    fun getOptions(): List<TGChooserDialogOption<T>>? =
        getAttribute(TGChooserDialogController.ATTRIBUTE_OPTIONS)

    fun onChooseInNewThread(handler: TGChooserDialogHandler<T>, value: T?) {
        Thread { handler.onChoose(value) }.start()
    }
}

@Composable
fun <T> TGChooserDialogContent(
    title: String?,
    options: List<TGChooserDialogOption<T>>,
    onChoose: (TGChooserDialogOption<T>) -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        if (!title.isNullOrEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
        }
        LazyColumn(modifier = Modifier.heightIn(max = 480.dp)) {
            itemsIndexed(options) { index, option ->
                ListItem(
                    headlineContent = { Text(option.label) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChoose(option) }
                        .padding(horizontal = 8.dp),
                )
                if (index < options.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGChooserDialogContentPreview() {
    GuitarLabTheme {
        TGChooserDialogContent(
            title = "Choose export format",
            options = listOf(
                TGChooserDialogOption("Guitar Pro", "gp"),
                TGChooserDialogOption("MusicXML", "musicxml"),
                TGChooserDialogOption("MIDI", "midi"),
            ),
            onChoose = {},
        )
    }
}
