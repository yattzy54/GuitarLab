package app.tuxguitar.android.view.dialog.info

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGSongInfoDialogController : TGComposeBottomSheetDialogController<TGSongInfoDialog>() {
    override fun createNewInstance(): TGSongInfoDialog = TGSongInfoDialog()
}
