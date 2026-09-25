package app.tuxguitar.android.view.dialog.channel

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGChannelEditDialogController : TGComposeBottomSheetDialogController<TGChannelEditDialog>() {
    override fun createNewInstance(): TGChannelEditDialog = TGChannelEditDialog()
}
