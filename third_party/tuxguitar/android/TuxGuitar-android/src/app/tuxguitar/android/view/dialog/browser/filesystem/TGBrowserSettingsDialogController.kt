package app.tuxguitar.android.view.dialog.browser.filesystem

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGBrowserSettingsDialogController : TGModalFragmentController<TGBrowserSettingsDialog>() {
    override fun createNewInstance(): TGBrowserSettingsDialog = TGBrowserSettingsDialog()

    companion object {
        @JvmField
        val ATTRIBUTE_HANDLER =
            app.tuxguitar.tools.browser.base.TGBrowserFactorySettingsHandler::class.java.name
        @JvmField
        val ATTRIBUTE_MOUNT_POINT = TGBrowserSettingsMountPoint::class.java.name
    }
}
