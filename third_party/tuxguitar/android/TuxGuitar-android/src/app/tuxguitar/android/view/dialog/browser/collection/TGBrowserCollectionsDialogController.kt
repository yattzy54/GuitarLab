package app.tuxguitar.android.view.dialog.browser.collection

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGBrowserCollectionsDialogController : TGModalFragmentController<TGBrowserCollectionsDialog>() {
    override fun createNewInstance(): TGBrowserCollectionsDialog = TGBrowserCollectionsDialog()
}
