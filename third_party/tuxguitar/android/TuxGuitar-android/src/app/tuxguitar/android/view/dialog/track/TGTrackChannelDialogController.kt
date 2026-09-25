package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGTrackChannelDialogController : TGModalFragmentController<TGTrackChannelDialog>() {
    override fun createNewInstance(): TGTrackChannelDialog = TGTrackChannelDialog()
}
