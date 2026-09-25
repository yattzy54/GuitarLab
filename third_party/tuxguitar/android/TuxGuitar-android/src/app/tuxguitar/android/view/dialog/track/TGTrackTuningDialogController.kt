package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGTrackTuningDialogController : TGModalFragmentController<TGTrackTuningDialog>() {
    override fun createNewInstance(): TGTrackTuningDialog = TGTrackTuningDialog()
}
