package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGTrackStringCountDialogController : TGModalFragmentController<TGTrackStringCountDialog>() {
    override fun createNewInstance(): TGTrackStringCountDialog = TGTrackStringCountDialog()
}
