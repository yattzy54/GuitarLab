package app.tuxguitar.android.view.dialog.browser.filesystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.browser.filesystem.TGFsBrowserSettings
import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogFragment
import app.tuxguitar.android.view.dialog.compose.TGDialogActionButtons
import app.tuxguitar.android.view.dialog.message.TGMessageDialogController
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.tools.browser.base.TGBrowserFactorySettingsHandler
import java.io.File

class TGBrowserSettingsDialog : TGComposeBottomSheetDialogFragment() {

    fun getMountPoint(): TGBrowserSettingsMountPoint =
        requireNotNull(
            getAttribute<TGBrowserSettingsMountPoint>(
                TGBrowserSettingsDialogController.ATTRIBUTE_MOUNT_POINT
            )
        )

    fun listFolders(path: File): List<TGBrowserSettingsFolderAdapterItem> {
        val mountPoint = getMountPoint()
        val items = mutableListOf<TGBrowserSettingsFolderAdapterItem>()
        if (path.exists() && path.isDirectory) {
            path.parentFile
                ?.takeIf { path != mountPoint.path }
                ?.let { items.add(TGBrowserSettingsFolderAdapterItem("../", it)) }

            val directoryFiles = path.listFiles()?.filter { it.isDirectory }?.toMutableList()
                ?: mutableListOf()
            directoryFiles.sortBy { it.name }
            directoryFiles.forEach { file ->
                items.add(TGBrowserSettingsFolderAdapterItem(file.name, file))
            }
        }
        return items
    }

    fun createSettings(name: String, path: String?): Boolean {
        if (name.isEmpty()) {
            showErrorMessage(R.string.browser_settings_fs_error_empty_name)
            return false
        }
        if (path.isNullOrEmpty()) {
            showErrorMessage(R.string.browser_settings_fs_error_empty_path)
            return false
        }

        val directory = File(path)
        if (!directory.exists()) {
            showErrorMessage(R.string.browser_settings_fs_error_nonexistent_path)
            return false
        }
        if (!directory.isDirectory) {
            showErrorMessage(R.string.browser_settings_fs_error_nonfolder_path)
            return false
        }

        val handler = requireNotNull(
            getAttribute<TGBrowserFactorySettingsHandler>(
                TGBrowserSettingsDialogController.ATTRIBUTE_HANDLER
            )
        )
        handler.onCreateSettings(TGFsBrowserSettings(name, path).toBrowserSettings())
        return true
    }

    fun showErrorMessage(message: Int) {
        showErrorMessage(R.string.browser_settings_fs_error_title, message)
    }

    fun showErrorMessage(title: Int, message: Int) {
        showErrorMessage(getString(title), getString(message))
    }

    fun showErrorMessage(title: String, message: String) {
        TGActionProcessor(findContext(), TGOpenDialogAction.NAME).apply {
            setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, activity)
            setAttribute(
                TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER,
                TGMessageDialogController()
            )
            setAttribute(TGMessageDialogController.ATTRIBUTE_TITLE, title)
            setAttribute(TGMessageDialogController.ATTRIBUTE_MESSAGE, message)
            process()
        }
    }

    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val mountPoint = getMountPoint()
        var currentPath by remember { mutableStateOf<File?>(mountPoint.path) }
        var name by remember { mutableStateOf(mountPoint.label) }
        val items = remember(currentPath) { currentPath?.let(::listFolders) ?: emptyList() }

        TGBrowserSettingsDialogContent(
            title = stringResource(R.string.browser_settings_fs_dlg_title),
            nameLabel = stringResource(R.string.browser_settings_fs_name_label),
            pathLabel = stringResource(R.string.browser_settings_fs_path_label),
            name = name,
            onNameChange = { name = it },
            pathPreview = currentPath?.absolutePath.orEmpty(),
            folderLabels = items.map { it.label },
            onFolderSelected = { index -> currentPath = items[index].file },
            onConfirm = {
                if (createSettings(name, currentPath?.absolutePath)) onDismiss()
            },
            onCancel = onDismiss,
        )
    }
}

@Composable
fun TGBrowserSettingsDialogContent(
    title: String,
    nameLabel: String,
    pathLabel: String,
    name: String,
    onNameChange: (String) -> Unit,
    pathPreview: String,
    folderLabels: List<String>,
    onFolderSelected: (Int) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(nameLabel) },
            singleLine = true,
        )
        Text(
            text = "$pathLabel $pathPreview",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
        )
        LazyColumn(modifier = Modifier.heightIn(max = 260.dp)) {
            items(folderLabels.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = { onFolderSelected(index) }) {
                        Icon(
                            painter = painterResource(R.drawable.browser_folder),
                            contentDescription = stringResource(
                                R.string.browser_element_icon_description
                            ),
                            modifier = Modifier.size(20.dp),
                        )
                        Text(text = folderLabels[index], modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
        TGDialogActionButtons(
            onConfirm = onConfirm,
            onCancel = onCancel,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TGBrowserSettingsDialogContentPreview() {
    MaterialTheme {
        TGBrowserSettingsDialogContent(
            title = "New file system source",
            nameLabel = "Name",
            pathLabel = "Path:",
            name = "My songs",
            onNameChange = {},
            pathPreview = "/storage/emulated/0/TuxGuitar",
            folderLabels = listOf("../", "Documents", "TuxGuitar", "Music"),
            onFolderSelected = {},
            onConfirm = {},
            onCancel = {},
        )
    }
}
