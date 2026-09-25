package app.tuxguitar.android.view.dialog.browser.collection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.R
import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.android.browser.model.TGBrowserFactory
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.android.view.dialog.compose.TGDialogDropdownField
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.song.models.TGChannel
import app.tuxguitar.tools.browser.TGBrowserCollection

class TGBrowserCollectionsDialog : TGComposeDialog() {
    private var eventListener: TGBrowserCollectionsEventListener? = null
    private var actionHandler: TGBrowserCollectionsActionHandler? = null
    private val collections = mutableStateListOf<TGBrowserCollection>()

    fun getChannel(): TGChannel? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL)

    override fun onShow() {
        super.onShow()
        actionHandler = TGBrowserCollectionsActionHandler(this)
        eventListener = TGBrowserCollectionsEventListener(this).also {
            TGActionManager.getInstance(findContext()).addPostExecutionListener(it)
        }
        refreshListView()
    }

    override fun onHide() {
        eventListener?.let { TGActionManager.getInstance(findContext()).removePostExecutionListener(it) }
        eventListener = null
        super.onHide()
    }

    fun createFactoryValues(): List<TGBrowserFactory> {
        val factoryValues = mutableListOf<TGBrowserFactory>()
        val factories = TGBrowserManager.getInstance(findContext()).getFactories()
        while (factories.hasNext()) {
            factoryValues.add(factories.next())
        }
        return factoryValues
    }

    fun createCollection(factory: TGBrowserFactory?) {
        factory?.createSettings(TGBrowserCollectionsSettingsHandler(this, factory.getType()))
    }

    fun addCollection(collection: TGBrowserCollection) {
        getActionHandler().createAddCollectionAction(collection).process()
    }

    fun removeCollection(collection: TGBrowserCollection) {
        getActionHandler().createRemoveCollectionAction(collection).process()
    }

    fun refreshListView() {
        collections.clear()
        val browserCollections = TGBrowserManager.getInstance(findContext()).getCollections()
        while (browserCollections.hasNext()) {
            collections.add(browserCollections.next())
        }
    }

    fun getActionHandler(): TGBrowserCollectionsActionHandler =
        requireNotNull(actionHandler) { "Browser collections dialog has not been initialized" }

    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val factories = remember { createFactoryValues() }

        TGBrowserCollectionsDialogContent(
            title = stringResource(R.string.browser_collections_dlg_title),
            factoryLabels = factories.map { it.getName() },
            collectionLabels = collections.map { it.settings.title },
            onAddCollection = { index -> createCollection(factories.getOrNull(index)) },
            onRemoveCollection = { index -> collections.getOrNull(index)?.let(::removeCollection) },
        )
    }
}

@Composable
fun TGBrowserCollectionsDialogContent(
    title: String,
    factoryLabels: List<String>,
    collectionLabels: List<String>,
    onAddCollection: (Int) -> Unit,
    onRemoveCollection: (Int) -> Unit,
) {
    var selectedFactoryIndex by remember(factoryLabels) { mutableIntStateOf(0) }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        if (factoryLabels.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                TGDialogDropdownField(
                    label = stringResource(R.string.browser_add_collection),
                    selectedText = factoryLabels[selectedFactoryIndex.coerceIn(0, factoryLabels.lastIndex)],
                    options = factoryLabels,
                    onOptionSelected = { selectedFactoryIndex = it },
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { onAddCollection(selectedFactoryIndex) }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.browser_add_collection),
                    )
                }
            }
        }

        LazyColumn(modifier = Modifier.heightIn(max = 300.dp).padding(top = 12.dp)) {
            items(collectionLabels.size) { index ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    Text(text = collectionLabels[index], modifier = Modifier.weight(1f))
                    IconButton(onClick = { onRemoveCollection(index) }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.browser_remove_collection),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGBrowserCollectionsDialogContentPreview() {
    MaterialTheme {
        TGBrowserCollectionsDialogContent(
            title = "Collection Browser",
            factoryLabels = listOf("File system", "Cloud storage"),
            collectionLabels = listOf("My songs", "Backup collection"),
            onAddCollection = {},
            onRemoveCollection = {},
        )
    }
}
