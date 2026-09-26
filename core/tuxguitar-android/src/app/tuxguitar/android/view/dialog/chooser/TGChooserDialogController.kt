package app.tuxguitar.android.view.dialog.chooser

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGChooserDialogController<T> : TGComposeBottomSheetDialogController<TGChooserDialog<T>>() {
    override fun createNewInstance(): TGChooserDialog<T> = TGChooserDialog()

    companion object {
        const val ATTRIBUTE_HANDLER = "app.tuxguitar.android.view.dialog.chooser.TGChooserDialogHandler"
        const val ATTRIBUTE_OPTIONS = "options"
        const val ATTRIBUTE_TITLE = "title"
    }
}
