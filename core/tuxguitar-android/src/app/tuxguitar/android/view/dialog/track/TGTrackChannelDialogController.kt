package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGTrackChannelDialogController : TGComposeBottomSheetDialogController<TGTrackChannelDialog>() {
    override fun createNewInstance(): TGTrackChannelDialog = TGTrackChannelDialog()
}
