package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGTrackNameDialogController : TGComposeBottomSheetDialogController<TGTrackNameDialog>() {
    override fun createNewInstance(): TGTrackNameDialog = TGTrackNameDialog()
}
