package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGTrackStringCountDialogController : TGComposeBottomSheetDialogController<TGTrackStringCountDialog>() {
    override fun createNewInstance(): TGTrackStringCountDialog = TGTrackStringCountDialog()
}
