package app.tuxguitar.android.view.dialog.channel

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGChannelEditDialogController : TGModalFragmentController<TGChannelEditDialog>() {
    override fun createNewInstance(): TGChannelEditDialog = TGChannelEditDialog()
}
