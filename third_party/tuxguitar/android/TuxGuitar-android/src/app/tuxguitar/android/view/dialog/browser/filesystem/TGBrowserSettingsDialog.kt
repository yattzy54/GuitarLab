package app.tuxguitar.android.view.dialog.browser.filesystem

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.browser.filesystem.TGFsBrowserSettings
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.dialog.message.TGMessageDialogController
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.tools.browser.base.TGBrowserFactorySettingsHandler
import java.io.File

class TGBrowserSettingsDialog : TGModalFragment(R.layout.view_browser_settings_fs_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.browser_settings_fs_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            if (createSettings()) close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        fillListView()
        fillDefaultNameValue()
    }

    fun fillListView() {
        val mountPoint = requireNotNull(
            getAttribute<TGBrowserSettingsMountPoint>(
                TGBrowserSettingsDialogController.ATTRIBUTE_MOUNT_POINT
            )
        )
        val adapter = TGBrowserSettingsFolderAdapter(requireView().context, mountPoint)
        adapter.setListener { path -> fillPathPreview(path?.absolutePath ?: "") }
        requireView().findViewById<ListView>(R.id.browser_settings_fs_path_value_selector)
            .adapter = adapter
    }

    fun fillDefaultNameValue() {
        val mountPoint = requireNotNull(
            getAttribute<TGBrowserSettingsMountPoint>(
                TGBrowserSettingsDialogController.ATTRIBUTE_MOUNT_POINT
            )
        )
        setTextFieldValue(R.id.browser_settings_fs_name_value, mountPoint.label)
    }

    fun setTextFieldValue(textFieldId: Int, value: String) {
        requireView().findViewById<EditText>(textFieldId).text.append(value)
    }

    fun setTextViewValue(textFieldId: Int, value: String) {
        requireView().findViewById<TextView>(textFieldId).text = value
    }

    fun getTextFieldValue(textFieldId: Int): String =
        requireView().findViewById<EditText>(textFieldId).text.toString()

    fun fillPathPreview(value: String) {
        setTextViewValue(R.id.browser_settings_fs_path_preview, value)
    }

    fun getNameValue(): String = getTextFieldValue(R.id.browser_settings_fs_name_value)

    fun getPathValue(): String? {
        val listView = requireView()
            .findViewById<ListView>(R.id.browser_settings_fs_path_value_selector)
        return (listView.adapter as TGBrowserSettingsFolderAdapter).getPath()?.absolutePath
    }

    fun createSettings(): Boolean {
        val name = getNameValue()
        val path = getPathValue()
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
}
