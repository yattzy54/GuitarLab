package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGTrackNameDialogController : TGModalFragmentController<TGTrackNameDialog>() {
    override fun createNewInstance(): TGTrackNameDialog = TGTrackNameDialog()
}
