package app.tuxguitar.android.view.dialog.browser.collection

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGBrowserCollectionsDialogController : TGComposeBottomSheetDialogController<TGBrowserCollectionsDialog>() {
    override fun createNewInstance(): TGBrowserCollectionsDialog = TGBrowserCollectionsDialog()
}
